let active = null;

async function postJson(url, body) {
  await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body)
  });
}

async function createStreamFromTabId(streamId, config) {
  const constraints = {
    audio: config.audio
      ? { mandatory: { chromeMediaSource: "tab", chromeMediaSourceId: streamId } }
      : false,
    video: {
      mandatory: {
        chromeMediaSource: "tab",
        chromeMediaSourceId: streamId,
        maxWidth: config.width,
        maxHeight: config.height,
        maxFrameRate: config.fps
      }
    }
  };
  return navigator.mediaDevices.getUserMedia(constraints);
}

async function startStreaming(config, streamId) {
  if (active && active.stop) {
    await active.stop();
  }
  const stream = await createStreamFromTabId(streamId, config);
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

chrome.runtime.onMessage.addListener((msg, _sender, sendResponse) => {
  const action = (msg && msg.action) || "";
  if (action === "__tr_offscreen_start") {
    startStreaming(msg.config, msg.streamId)
      .then(() => sendResponse({ status: "started" }))
      .catch((err) => sendResponse({ status: "error", reason: err && err.message ? err.message : String(err) }));
    return true;
  }
  if (action === "__tr_offscreen_stop") {
    const done = active && active.stop ? active.stop() : Promise.resolve();
    done.finally(() => {
      active = null;
      sendResponse({ status: "stopped" });
    });
    return true;
  }
  return false;
});
