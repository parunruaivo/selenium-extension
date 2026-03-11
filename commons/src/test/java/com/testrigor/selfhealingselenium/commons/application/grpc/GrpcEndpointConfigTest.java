package com.testrigor.selfhealingselenium.commons.application.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.testng.annotations.Test;

import com.testrigor.seleniumextension.commons.application.grpc.GrpcEndpointConfig;
import com.typesafe.config.Config;

@SuppressWarnings("checkstyle:MagicNumber")
public class GrpcEndpointConfigTest {

	@Test
	public void defaultProduction_usesStandardHostAndPort443() {
		GrpcEndpointConfig cfg = GrpcEndpointConfig.defaultProduction();
		assertThat(cfg.getHost()).isEqualTo(GrpcEndpointConfig.DEFAULT_GRPC_HOST);
		assertThat(cfg.getPort()).isEqualTo(443);
		assertThat(cfg.getUseTls()).isNull();
		assertThat(cfg.useTransportSecurity()).isTrue();
	}

	@Test
	public void fromConfig_blankUriFallsBackToDefaultHost() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.uri")).thenReturn(true);
		when(config.getString("testrigor.grpc.uri")).thenReturn("  ");
		when(config.hasPath("testrigor.grpc.port")).thenReturn(true);
		when(config.getInt("testrigor.grpc.port")).thenReturn(9091);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);

		GrpcEndpointConfig cfg = GrpcEndpointConfig.fromConfig(config);
		assertThat(cfg.getHost()).isEqualTo(GrpcEndpointConfig.DEFAULT_GRPC_HOST);
		assertThat(cfg.getPort()).isEqualTo(9091);
		assertThat(cfg.useTransportSecurity()).isFalse();
	}

	@Test
	public void fromConfig_missingPortFallsBackTo443() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.uri")).thenReturn(true);
		when(config.getString("testrigor.grpc.uri")).thenReturn("grpc.example.test");
		when(config.hasPath("testrigor.grpc.port")).thenReturn(false);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);

		GrpcEndpointConfig cfg = GrpcEndpointConfig.fromConfig(config);
		assertThat(cfg.getHost()).isEqualTo("grpc.example.test");
		assertThat(cfg.getPort()).isEqualTo(GrpcEndpointConfig.DEFAULT_GRPC_PORT);
		assertThat(cfg.useTransportSecurity()).isTrue();
	}

	@Test
	public void fromConfig_missingUriFallsBackToDefaultHost() {
		Config config = mock(Config.class);
		when(config.hasPath("testrigor.grpc.uri")).thenReturn(false);
		when(config.hasPath("testrigor.grpc.port")).thenReturn(true);
		when(config.getInt("testrigor.grpc.port")).thenReturn(8443);
		when(config.hasPath("testrigor.grpc.use-tls")).thenReturn(false);

		GrpcEndpointConfig cfg = GrpcEndpointConfig.fromConfig(config);
		assertThat(cfg.getHost()).isEqualTo(GrpcEndpointConfig.DEFAULT_GRPC_HOST);
		assertThat(cfg.getPort()).isEqualTo(8443);
	}
}
