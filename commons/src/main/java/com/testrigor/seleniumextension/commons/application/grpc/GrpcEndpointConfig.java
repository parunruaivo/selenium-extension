package com.testrigor.seleniumextension.commons.application.grpc;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Objects;

import com.typesafe.config.Config;

/**
 * Immutable gRPC endpoint for the selenium-extension service (code-first alternative to HOCON paths).
 */
public final class GrpcEndpointConfig {

	public static final String DEFAULT_GRPC_HOST = "selenium-extension.testrigor.com";
	public static final int DEFAULT_GRPC_PORT = 443;

	private final String host;
	private final int port;
	/** When null, TLS matches legacy behavior: enabled for port 443, plaintext otherwise. */
	private final Boolean useTls;

	private GrpcEndpointConfig(String hostValue, int portValue, Boolean useTlsValue) {
		if (isBlank(hostValue)) {
			throw new IllegalArgumentException("gRPC host must not be blank");
		}
		if (portValue < 1 || portValue > 65535) {
			throw new IllegalArgumentException("gRPC port must be between 1 and 65535: " + portValue);
		}
		host = hostValue.trim();
		port = portValue;
		useTls = useTlsValue;
	}

	/**
	 * Production defaults: {@value DEFAULT_GRPC_HOST}:{@value DEFAULT_GRPC_PORT} with TLS (port 443 rule).
	 */
	public static GrpcEndpointConfig defaultProduction() {
		return new GrpcEndpointConfig(DEFAULT_GRPC_HOST, DEFAULT_GRPC_PORT, null);
	}

	public static GrpcEndpointConfig of(String hostValue, int portValue) {
		return of(hostValue, portValue, null);
	}

	public static GrpcEndpointConfig of(String hostValue, int portValue, Boolean useTlsValue) {
		return new GrpcEndpointConfig(hostValue, portValue, useTlsValue);
	}

	/**
	 * Reads {@code testrigor.grpc.uri}, {@code testrigor.grpc.port}, optional {@code testrigor.grpc.use-tls}.
	 * Missing or blank URI falls back to {@link #DEFAULT_GRPC_HOST}; missing port falls back to {@link #DEFAULT_GRPC_PORT}.
	 */
	public static GrpcEndpointConfig fromConfig(Config config) {
		String uri = "";
		if (config.hasPath("testrigor.grpc.uri")) {
			uri = config.getString("testrigor.grpc.uri");
		}
		String hostValue = isBlank(uri) ? DEFAULT_GRPC_HOST : uri.trim();
		int portValue = config.hasPath("testrigor.grpc.port")
			? config.getInt("testrigor.grpc.port")
			: DEFAULT_GRPC_PORT;
		Boolean useTlsValue = config.hasPath("testrigor.grpc.use-tls")
			? config.getBoolean("testrigor.grpc.use-tls")
			: null;
		return new GrpcEndpointConfig(hostValue, portValue, useTlsValue);
	}

	/**
	 * Same rule as legacy {@code TestRigorGrpcClient.useTls}: explicit flag wins, else TLS for port 443 only.
	 */
	public boolean useTransportSecurity() {
		return resolveUseTls(port, useTls);
	}

	public static boolean resolveUseTls(int portValue, Boolean useTlsOverride) {
		if (useTlsOverride != null) {
			return useTlsOverride;
		}
		return portValue == 443;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Boolean getUseTls() {
		return useTls;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		GrpcEndpointConfig that = (GrpcEndpointConfig) o;
		return port == that.port && host.equals(that.host) && Objects.equals(useTls, that.useTls);
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port, useTls);
	}
}
