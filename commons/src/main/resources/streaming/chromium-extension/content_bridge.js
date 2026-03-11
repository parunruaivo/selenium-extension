window.addEventListener("message", (event) => {
  if (event.source !== window) {
    return;
  }
  const payload = event.data;
  if (!payload || payload.__trStudioBridge !== true) {
    return;
  }
  chrome.runtime.sendMessage(
    {
      action: payload.action,
      config: payload.config
    },
    (response) => {
      const runtimeError = chrome.runtime && chrome.runtime.lastError
        ? chrome.runtime.lastError.message
        : null;
      window.postMessage(
        {
          __trStudioBridgeResponse: true,
          requestId: payload.requestId,
          runtimeError,
          response
        },
        "*"
      );
    }
  );
});

chrome.runtime.onMessage.addListener((msg) => {
  if (msg && msg.__trSetStreamState !== undefined) {
    window.__trStudioStreamState = msg.__trSetStreamState;
  }
});
