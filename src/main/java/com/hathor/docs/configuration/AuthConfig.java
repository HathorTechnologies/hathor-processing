package com.hathor.docs.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hathor.docs.auth.JwtAuthFilter;
import com.hathor.docs.auth.JwtFilter;
import com.hathor.docs.controllers.Api;
import com.hathor.docs.properties.AuthProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import ua.ardas.jwt.CookiesGetter;
import ua.ardas.jwt.JwtService;
import ua.ardas.jwt.RsaKeyStorageBuilder;

import javax.servlet.Filter;
import java.util.HashSet;
import java.util.Set;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AuthConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;

    private static final Set<String> EXCLUDE_PATHS = new HashSet<>();

    static {
        EXCLUDE_PATHS.add(Api.ROOT_PATH + Api.BuildVersion.VERSION);
    }

	private final AuthProperties authProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
	}

    @Bean
    public CookiesGetter cookiesGetter(){
        return new CookiesGetter();
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean jwtFilter(JwtService jwtService) {
        return createFilter(new JwtFilter(EXCLUDE_PATHS, jwtService));
    }

    @Bean
    @Order(3)
    public FilterRegistrationBean setAuthForSecurityFilter(JwtService jwtService) {
        return createFilter(new JwtAuthFilter(jwtService, EXCLUDE_PATHS));
    }

    private FilterRegistrationBean createFilter(Filter filter) {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(Api.ROOT_PATH + "/*");

        return registrationBean;
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService(new RsaKeyStorageBuilder()
                .publicKey(authProperties.getPublicKeyPath())
                .build(), authProperties.getTokenExpireHours(), objectMapper);
    }
}
