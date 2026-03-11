package com.testrigor.seleniumextension.commons.application.streaming;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class StreamingCoordinator {

	private final StreamingSettings settings;
	private final StudioSignalingClient signalingClient;
	private final BrowserStreamingStrategy chromiumPrimary;
	private final DisplayMediaFallbackStreamingStrategy fallbackStrategy;
	private volatile boolean started;
	private volatile String activeStrategyName;

	public StreamingCoordinator(StreamingSettings settings) {
		this.settings = settings;
		this.signalingClient = new StudioSignalingClient();
		this.chromiumPrimary = new ChromiumPrimaryStreamingStrategy();
		this.fallbackStrategy = new DisplayMediaFallbackStreamingStrategy();
	}

	public static StreamingCoordinator fromConfig(Config config) {
		return new StreamingCoordinator(StreamingSettings.fromConfig(config));
	}

	public boolean start(BrowserScriptExecutor executor, String sessionId) {
		if (!settings.isEnabled()) {
			return false;
		}
		if (started) {
			return true;
		}
		if (!signalingClient.isAvailable(settings)) {
			log.warn("Streaming disabled for session {}: signaling endpoint {} is unavailable", sessionId, settings.getInfoUrl());
			return false;
		}

		String browserName = executor.getBrowserName();
		for (BrowserStreamingStrategy strategy : orderedStrategies(browserName)) {
			if (!strategy.supports(browserName)) {
				continue;
			}
			StreamStartResult result = strategy.start(executor, settings, sessionId);
			if (result.isSuccess()) {
				started = true;
				activeStrategyName = result.getStrategy();
				log.info("Streaming started for session {} using {} strategy", sessionId, activeStrategyName);
				return true;
			}
			log.warn("Streaming strategy {} failed for session {}: {}", strategy.name(), sessionId, result.getMessage());
			if ("chromium-primary".equals(strategy.name()) && settings.isChromiumExtensionRequired()) {
				log.warn("Chromium extension is required; skipping fallback for session {}", sessionId);
				break;
			}
		}
		log.warn("Unable to start streaming for session {} (browser={})", sessionId, browserName);
		return false;
	}

	public void stop(BrowserScriptExecutor executor) {
		if (!settings.isEnabled()) {
			return;
		}
		if ("chromium-primary".equals(activeStrategyName)) {
			chromiumPrimary.stop(executor);
		} else {
			fallbackStrategy.stop(executor);
		}
		started = false;
		activeStrategyName = null;
	}

	private List<BrowserStreamingStrategy> orderedStrategies(String browserName) {
		List<BrowserStreamingStrategy> strategies = new ArrayList<>();
		switch (settings.getMode()) {
			case DISPLAY_MEDIA_ONLY:
				strategies.add(fallbackStrategy);
				break;
			case CHROMIUM_PRIMARY:
				strategies.add(chromiumPrimary);
				strategies.add(fallbackStrategy);
				break;
			case AUTO:
			default:
				if (chromiumPrimary.supports(browserName)) {
					strategies.add(chromiumPrimary);
					strategies.add(fallbackStrategy);
				} else {
					strategies.add(fallbackStrategy);
				}
				break;
		}
		return strategies;
	}
}
