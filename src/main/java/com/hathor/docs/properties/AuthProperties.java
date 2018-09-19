package com.hathor.docs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("auth")
public class AuthProperties {

    private String publicKeyPath;
    private int tokenExpireHours;
}