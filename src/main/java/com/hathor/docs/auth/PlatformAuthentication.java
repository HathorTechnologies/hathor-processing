package com.hathor.docs.auth;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class PlatformAuthentication implements Authentication {

    public static final String USER = "USER";
    public static final String NODE = "NODE";

    private boolean authenticated = true;

    private final AuthJwtToken authJwtToken;

    public PlatformAuthentication(AuthJwtToken authJwtToken) {
        this.authJwtToken = authJwtToken;
    }

    public UUID getNodeId() {
        return authJwtToken.getNodeId();
    }

    public Integer getUserId() {
        return authJwtToken.getUserId();
    }

    public Integer getOwnerId() {
        return authJwtToken.getOwnerId();
    }

    @Override
    public String getName() {
        return StringUtils.isNotBlank(authJwtToken.getEmail())
                ? authJwtToken.getEmail()
                : authJwtToken.getNodeId().toString();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Objects.nonNull(getUserId())) {
            if (CollectionUtils.isEmpty(authJwtToken.getPermissions())) {
                return Collections.emptySet();
            }
            Set<SimpleGrantedAuthority> authoritySet = authJwtToken.getPermissions().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(toSet());
            authoritySet.add(new SimpleGrantedAuthority(USER));
            return authoritySet;
        }
        return Collections.singletonList(new SimpleGrantedAuthority(NODE));
    }

    @Override
    public Object getCredentials() {
        return Objects.isNull(authJwtToken.getUserId())
                ? authJwtToken.getNodeId()
                : authJwtToken.getUserId();
    }

    @Override
    public AuthJwtToken getDetails() {
        return authJwtToken;
    }

    @Override
    public Object getPrincipal() {
        return Objects.isNull(authJwtToken.getUserId())
                ? authJwtToken.getNodeId()
                : authJwtToken.getUserId();
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}