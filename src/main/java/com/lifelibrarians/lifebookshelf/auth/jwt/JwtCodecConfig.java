package com.lifelibrarians.lifebookshelf.auth.jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtCodecConfig {

	private final static MacAlgorithm algorithm = MacAlgorithm.HS256;
	private final SecretKey key;

	public JwtCodecConfig(
			@Value("${jwt.key}") String secretKey
	) {
		this.key = new SecretKeySpec(secretKey.getBytes(), algorithm.getName());
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder
				.withSecretKey(key)
				.macAlgorithm(algorithm)
				.build();
	}

	@Bean
	public JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(key));
	}
}
