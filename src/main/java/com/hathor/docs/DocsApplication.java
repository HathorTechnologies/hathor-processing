package com.hathor.docs;

import com.hathor.docs.properties.AuthProperties;
import com.hathor.docs.properties.BasicAuthProperties;
import com.hathor.docs.properties.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableScheduling
@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableConfigurationProperties({StorageProperties.class, AuthProperties.class, BasicAuthProperties.class})
public class DocsApplication {

    public static void main( String[] args ) {
        SpringApplication.run(DocsApplication.class, args);
    }
}
