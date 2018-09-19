package com.hathor.docs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.docs.properties.AuthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.ardas.jwt.JwtService;
import ua.ardas.jwt.RsaKeyStorageBuilder;

@Configuration
@EnableConfigurationProperties({AuthProperties.class})
public class TestConfig {

	private final AuthProperties authProperties;
	private final ObjectMapper objectMapper;

	@Autowired
	public TestConfig(AuthProperties authProperties, ObjectMapper objectMapper) {
		this.authProperties = authProperties;
		this.objectMapper = objectMapper;
	}

	@Bean
	public JwtService jwtService(@Value("${auth.private-key-path}") String privateKeyPath) {
		return new JwtService(
		        new RsaKeyStorageBuilder().privateKey(privateKeyPath).publicKey(authProperties.getPublicKeyPath()).build(),
                authProperties.getTokenExpireHours(),
                objectMapper
        );
	}
}