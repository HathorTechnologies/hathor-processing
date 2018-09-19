package com.hathor.docs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("basic-auth")
public class BasicAuthProperties {
    private String username;
    private String password;
}
