package com.hathor.docs.auth;

import org.springframework.web.filter.GenericFilterBean;
import ua.ardas.jwt.JwtAuthFilter;
import ua.ardas.jwt.JwtAuthFilterParams;
import ua.ardas.jwt.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collection;

public class JwtFilter extends GenericFilterBean {

    private final JwtAuthFilter<AuthJwtToken> jwtAuthFilter;


    public JwtFilter(Collection<String> excludeUrls, JwtService jwtService) {
        JwtAuthFilterParams<AuthJwtToken> jwtAuthFilterParams = JwtAuthFilterParams
                .builder(AuthJwtToken.class, jwtService)
                .excludedUrls(excludeUrls)
                .build();
        jwtAuthFilter = new JwtAuthFilter<>(jwtAuthFilterParams);
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        jwtAuthFilter.filterRequest(req, res, chain);
    }
}