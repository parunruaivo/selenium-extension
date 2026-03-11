package com.testrigor.seleniumextension.commons.application.streaming;

import java.util.Map;

public class ChromiumPrimaryStreamingStrategy implements BrowserStreamingStrategy {

	/** Dispatches Ctrl+Shift+S so extension command can run with user-gesture context. */
	private static final String SEND_STREAM_SHORTCUT_SCRIPT =
			"(function(){"
			+ "var opts={key:'S',code:'KeyS',keyCode:83,ctrlKey:true,shiftKey:true,bubbles:true};"
			+ "document.body.dispatchEvent(new KeyboardEvent('keydown',opts));"
			+ "document.body.dispatchEvent(new KeyboardEvent('keyup',opts));"
			+ "})();";

	private static final String START_SCRIPT =
			"var cfg = arguments[0];"
			+ "window.__trStudioStreamState='starting';"
			+ "var requestId = 'tr-' + Date.now() + '-' + Math.random().toString(36).slice(2);"
			+ "window.__trStudioBridgeResponseHandler = function(event){"
			+ "  var data = event && event.data;"
			+ "  if (!data || data.__trStudioBridgeResponse !== true || data.requestId !== requestId) { return; }"
			+ "  window.removeEventListener('message', window.__trStudioBridgeResponseHandler);"
			+ "  if (data.runtimeError) { window.__trStudioStreamState='error:EXTENSION_NOT_INSTALLED'; return; }"
			+ "  var resp = data.response;"
			+ "  if (!resp || !resp.status) { window.__trStudioStreamState='error:INVALID_EXTENSION_RESPONSE'; return; }"
			+ "  if (resp.status==='started') { window.__trStudioStreamState='started'; return; }"
			+ "  if (resp.status==='need_gesture') { window.__trStudioStreamState='need_gesture'; return; }"
			+ "  if (resp.status==='starting') { window.__trStudioStreamState='starting'; return; }"
			+ "  window.__trStudioStreamState='error:' + (resp.reason || 'UNKNOWN_ERROR');"
			+ "};"
			+ "window.addEventListener('message', window.__trStudioBridgeResponseHandler);"
			+ "window.postMessage({__trStudioBridge:true,requestId:requestId,action:cfg.actionStart,config:cfg}, '*');"
			+ "return window.__trStudioStreamState;";

	private static final String STOP_SCRIPT =
			"var cfg = arguments[0] || {};"
			+ "try { window.postMessage({__trStudioBridge:true,requestId:'tr-stop-'+Date.now(),action:cfg.actionStop}, '*'); } catch (_e) {}"
			+ "window.__trStudioStreamState='stopped';"
			+ "return true;";

	@Override
	public String name() {
		return "chromium-primary";
	}

	@Override
	public boolean supports(String browserName) {
		String normalized = browserName == null ? "" : browserName.toLowerCase();
		return normalized.contains("chrome") || normalized.contains("edge");
	}

	@Override
	public StreamStartResult start(BrowserScriptExecutor executor, StreamingSettings settings, String sessionId) {
		if (!settings.isChromiumExtensionEnabled()) {
			return StreamStartResult.failure(name(), "EXTENSION_DISABLED");
		}
		Map<String, Object> payload = settings.toJsConfig(sessionId, true);
		try {
			executor.executeScript(START_SCRIPT, payload);
			long deadline = System.currentTimeMillis() + settings.getStartupTimeoutMs();
			boolean sentShortcut = false;
			while (System.currentTimeMillis() < deadline) {
				String state = String.valueOf(executor.executeScript("return window.__trStudioStreamState || 'none';"));
				if (ChromiumExtensionContract.STATE_STARTED.equals(state)) {
					return StreamStartResult.success(name());
				}
				if (state.startsWith("error:")) {
					return StreamStartResult.failure(name(), state.substring("error:".length()));
				}
				if ("need_gesture".equals(state) && !sentShortcut) {
					sentShortcut = true;
					try {
						executor.executeScript(SEND_STREAM_SHORTCUT_SCRIPT);
					} catch (RuntimeException ignored) {
						// best effort
					}
				}
				Thread.sleep(settings.getPollIntervalMs());
			}
			return StreamStartResult.failure(name(), "startup-timeout");
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return StreamStartResult.failure(name(), "interrupted");
		} catch (RuntimeException ex) {
			return StreamStartResult.failure(name(), ex.getMessage());
		}
	}

	@Override
	public void stop(BrowserScriptExecutor executor) {
		try {
			executor.executeScript(STOP_SCRIPT, java.util.Map.of());
		} catch (RuntimeException ignored) {
			// best effort
		}
	}
}
