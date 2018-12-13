package com.hathor.docs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.docs.properties.AuthProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ua.ardas.jwt.JwtService;
import ua.ardas.jwt.RsaKeyStorageBuilder;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({AuthProperties.class})
public class TestConfig {

	private final AuthProperties authProperties;
	private final ObjectMapper objectMapper;

	@Bean
	@Primary
	public JwtService jwtService(@Value("${auth.private-key-path}") String privateKeyPath) {
		return new JwtService(
		        new RsaKeyStorageBuilder().privateKey(privateKeyPath).publicKey(authProperties.getPublicKeyPath()).build(),
                authProperties.getTokenExpireHours(),
                objectMapper
        );
	}
}