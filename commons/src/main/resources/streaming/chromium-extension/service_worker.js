let active = null;
/** When getMediaStreamId fails (no user gesture), store config so command handler can retry. */
let pendingStream = null; // { config, tabId }

async function postJson(url, body) {
  await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
}

async function startLegacyCapture(config) {
  const stream = await new Promise((resolve, reject) => {
    chrome.tabCapture.capture(
      {
        audio: !!config.audio,
        video: true,
        videoConstraints: {
          mandatory: {
            maxWidth: config.width,
            maxHeight: config.height,
            maxFrameRate: config.fps
          }
        }
      },
      (capturedStream) => {
        if (chrome.runtime.lastError) {
          reject(new Error(chrome.runtime.lastError.message));
          return;
        }
        if (!capturedStream) {
          reject(new Error("capture returned empty stream"));
          return;
        }
        resolve(capturedStream);
      }
    );
  });

  const pc = new RTCPeerConnection({ iceServers: [{ urls: "stun:stun.l.google.com:19302" }] });
  stream.getTracks().forEach((t) => pc.addTrack(t, stream));
  pc.onicecandidate = (e) => {
    if (!e.candidate) return;
    postJson(config.iceUrl, {
      candidate: e.candidate.candidate,
      sdpMid: e.candidate.sdpMid,
      sdpMLineIndex: e.candidate.sdpMLineIndex,
      sessionId: config.sessionId
    }).catch(() => {});
  };

  const offer = await pc.createOffer();
  await pc.setLocalDescription(offer);
  const offerResp = await fetch(config.offerUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sdp: offer.sdp, type: "offer", sessionId: config.sessionId })
  });
  if (!offerResp.ok) {
    throw new Error(`offer http ${offerResp.status}`);
  }
  const answer = await offerResp.json();
  if (!answer || !answer.sdp) {
    throw new Error("missing answer sdp");
  }
  await pc.setRemoteDescription({ type: "answer", sdp: answer.sdp });

  active = {
    mode: "legacy",
    stream,
    pc,
    stop: async () => {
      try {
        pc.getSenders().forEach((s) => s.track && s.track.stop());
      } catch (_e) {}
      try {
        stream.getTracks().forEach((t) => t.stop());
      } catch (_e) {}
      try {
        pc.close();
      } catch (_e) {}
    }
  };
}

async function hasOffscreenDocument() {
  if (!chrome.runtime.getContexts) {
    return false;
  }
  const contexts = await chrome.runtime.getContexts({
    contextTypes: ["OFFSCREEN_DOCUMENT"],
    documentUrls: [chrome.runtime.getURL("offscreen.html")]
  });
  return Array.isArray(contexts) && contexts.length > 0;
}

async function ensureOffscreenDocument() {
  if (await hasOffscreenDocument()) {
    return;
  }
  await chrome.offscreen.createDocument({
    url: "offscreen.html",
    reasons: ["USER_MEDIA"],
    justification: "Capture tab stream for automated testRigor streaming"
  });
}

async function sendToOffscreen(message) {
  return new Promise((resolve, reject) => {
    chrome.runtime.sendMessage(message, (resp) => {
      const err = chrome.runtime.lastError;
      if (err) {
        reject(new Error(err.message));
        return;
      }
      resolve(resp || {});
    });
  });
}

async function getActiveTabId(senderTabId) {
  if (senderTabId != null) {
    return senderTabId;
  }
  const tabs = await chrome.tabs.query({ active: true, lastFocusedWindow: true });
  if (!tabs || tabs.length === 0 || tabs[0].id == null) {
    throw new Error("ACTIVE_TAB_NOT_FOUND");
  }
  return tabs[0].id;
}

async function getStreamId(tabId) {
  return new Promise((resolve, reject) => {
    chrome.tabCapture.getMediaStreamId(
      { targetTabId: tabId },
      (streamId) => {
      if (chrome.runtime.lastError) {
        reject(new Error(chrome.runtime.lastError.message));
        return;
      }
      if (!streamId) {
        reject(new Error("EMPTY_STREAM_ID"));
        return;
      }
      resolve(streamId);
    });
  });
}

async function startOffscreenCapture(config, senderTabId) {
  await ensureOffscreenDocument();
  const tabId = await getActiveTabId(senderTabId);
  const streamId = await getStreamId(tabId);
  const response = await sendToOffscreen({
    action: "__tr_offscreen_start",
    config,
    streamId
  });
  if (!response || response.status !== "started") {
    throw new Error((response && response.reason) || "OFFSCREEN_START_FAILED");
  }
  active = {
    mode: "offscreen",
    stop: async () => {
      try {
        await sendToOffscreen({ action: "__tr_offscreen_stop" });
      } catch (_e) {}
    }
  };
}

async function startStreaming(config, senderTabId) {
  if (active && active.stop) {
    await active.stop();
  }
  if (chrome.tabCapture && typeof chrome.tabCapture.capture === "function") {
    await startLegacyCapture(config);
    return;
  }
  if (chrome.tabCapture && typeof chrome.tabCapture.getMediaStreamId === "function") {
    try {
      await startOffscreenCapture(config, senderTabId);
      return;
    } catch (e) {
      const msg = (e && e.message) || String(e);
      if (/invoked|activeTab|Chrome pages cannot be captured/i.test(msg)) {
        const tabId = await getActiveTabId(senderTabId);
        pendingStream = { config, tabId };
        throw new Error("NEED_GESTURE");
      }
      throw e;
    }
  }
  throw new Error("TAB_CAPTURE_API_UNAVAILABLE");
}

function notifyTabState(tabId, state) {
  if (tabId == null) return;
  try {
    chrome.tabs.sendMessage(tabId, { __trSetStreamState: state });
  } catch (_e) {}
}

chrome.commands.onCommand.addListener((command) => {
  if (command !== "start-streaming" || !pendingStream) return;
  const { config, tabId } = pendingStream;
  pendingStream = null;
  getStreamId(tabId)
    .then((streamId) => {
      return ensureOffscreenDocument().then(() =>
        sendToOffscreen({ action: "__tr_offscreen_start", config, streamId })
      );
    })
    .then((response) => {
      if (response && response.status === "started") {
        active = {
          mode: "offscreen",
          stop: () => sendToOffscreen({ action: "__tr_offscreen_stop" }).catch(() => {})
        };
        notifyTabState(tabId, "started");
      } else {
        notifyTabState(tabId, "error:" + ((response && response.reason) || "OFFSCREEN_START_FAILED"));
      }
    })
    .catch((err) => {
      notifyTabState(tabId, "error:" + ((err && err.message) || String(err)));
    });
});

chrome.runtime.onMessageExternal.addListener((msg, _sender, sendResponse) => {
  return handleMessage(msg, _sender, sendResponse);
});

chrome.runtime.onMessage.addListener((msg, _sender, sendResponse) => {
  return handleMessage(msg, _sender, sendResponse);
});

function handleMessage(msg, sender, sendResponse) {
  const action = (msg && msg.action) || "";
  if (action === "__tr_offscreen_start" || action === "__tr_offscreen_stop") {
    // let offscreen document listener handle these messages
    return false;
  }
  const config = msg && msg.config;
  if (action === "startStreaming") {
    const senderTabId = sender && sender.tab && sender.tab.id != null ? sender.tab.id : null;
    startStreaming(config, senderTabId)
      .then(() => sendResponse({ status: "started" }))
      .catch((err) => {
        const reason = err && err.message ? err.message : String(err);
        sendResponse(reason === "NEED_GESTURE" ? { status: "need_gesture" } : { status: "error", reason });
      });
    return true;
  }
  if (action === "stopStreaming") {
    const done = active && active.stop ? active.stop() : Promise.resolve();
    done.finally(() => {
      active = null;
      sendResponse({ status: "stopped" });
    });
    return true;
  }
  sendResponse({ status: "error", reason: "unknown action" });
  return false;
}
