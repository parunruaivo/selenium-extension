package com.testrigor.seleniumextension.commons.application.streaming;

import java.util.Map;

public class DisplayMediaFallbackStreamingStrategy implements BrowserStreamingStrategy {

	protected static final String START_SCRIPT =
			"var cfg = arguments[0];"
			+ "window.__trStudioStreamState = 'starting';"
			+ "(async function(){"
			+ " try {"
			+ "  if (!navigator.mediaDevices || !navigator.mediaDevices.getDisplayMedia) { throw new Error('getDisplayMedia unsupported'); }"
			+ "  if (window.__trStudioStream && window.__trStudioStream.stop) { await window.__trStudioStream.stop(); }"
			+ "  var video = {width:{ideal:cfg.width},height:{ideal:cfg.height},frameRate:{ideal:cfg.fps,max:cfg.fps}};"
			+ "  var opts = {video:video,audio:!!cfg.audio};"
			+ "  if (cfg.chromiumHints) { opts.displaySurface='browser'; opts.selfBrowserSurface='include'; opts.preferCurrentTab=true; video.displaySurface='browser'; video.preferCurrentTab=true; }"
			+ "  var media = await navigator.mediaDevices.getDisplayMedia(opts);"
			+ "  var pc = new RTCPeerConnection({iceServers:[{urls:'stun:stun.l.google.com:19302'}]});"
			+ "  media.getTracks().forEach(function(track){ pc.addTrack(track, media); });"
			+ "  pc.onicecandidate = function(ev){"
			+ "   if (!ev.candidate) { return; }"
			+ "   fetch(cfg.iceUrl,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({candidate:ev.candidate.candidate,sdpMid:ev.candidate.sdpMid,sdpMLineIndex:ev.candidate.sdpMLineIndex,sessionId:cfg.sessionId})}).catch(function(){});"
			+ "  };"
			+ "  var offer = await pc.createOffer();"
			+ "  await pc.setLocalDescription(offer);"
			+ "  var resp = await fetch(cfg.offerUrl,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({sdp:offer.sdp,type:'offer',sessionId:cfg.sessionId})});"
			+ "  if (!resp.ok) { throw new Error('offer http '+resp.status); }"
			+ "  var answer = await resp.json();"
			+ "  if (!answer || !answer.sdp) { throw new Error('missing answer sdp'); }"
			+ "  await pc.setRemoteDescription({type:'answer',sdp:answer.sdp});"
			+ "  window.__trStudioStream = {"
			+ "   pc: pc,"
			+ "   media: media,"
			+ "   stop: async function(){"
			+ "    try{ pc.getSenders().forEach(function(s){ if (s.track) { s.track.stop(); } }); }catch(_e){}"
			+ "    try{ media.getTracks().forEach(function(t){ t.stop(); }); }catch(_e){}"
			+ "    try{ pc.close(); }catch(_e){}"
			+ "    window.__trStudioStreamState='stopped';"
			+ "   }"
			+ "  };"
			+ "  window.__trStudioStreamState='started';"
			+ " } catch (e) {"
			+ "  var msg = (e && e.message) ? e.message : String(e);"
			+ "  window.__trStudioStreamState='error:' + msg;"
			+ " }"
			+ "})();"
			+ "return window.__trStudioStreamState;";

	protected static final String STOP_SCRIPT =
			"if (window.__trStudioStream && window.__trStudioStream.stop) { window.__trStudioStream.stop(); }"
			+ "window.__trStudioStreamState='stopped';"
			+ "return true;";

	@Override
	public String name() {
		return "display-media";
	}

	@Override
	public boolean supports(String browserName) {
		return true;
	}

	@Override
	public StreamStartResult start(BrowserScriptExecutor executor, StreamingSettings settings, String sessionId) {
		// Use chromiumHints so getDisplayMedia prefers current tab / browser window (picker shows usable options)
		Map<String, Object> payload = settings.toJsConfig(sessionId, true);
		return executeAndPoll(executor, settings, payload);
	}

	protected StreamStartResult executeAndPoll(BrowserScriptExecutor executor, StreamingSettings settings, Map<String, Object> payload) {
		try {
			executor.executeScript(START_SCRIPT, payload);
			long deadline = System.currentTimeMillis() + settings.getStartupTimeoutMs();
			while (System.currentTimeMillis() < deadline) {
				String state = String.valueOf(executor.executeScript("return window.__trStudioStreamState || 'none';"));
				if ("started".equals(state)) {
					return StreamStartResult.success(name());
				}
				if (state.startsWith("error:")) {
					return StreamStartResult.failure(name(), state);
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

	public void stop(BrowserScriptExecutor executor) {
		try {
			executor.executeScript(STOP_SCRIPT);
		} catch (RuntimeException ignored) {
			// best effort
		}
	}
}
