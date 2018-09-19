package com.hathor.docs.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

public class UserAuthentication implements Authentication {

	private boolean authenticated = true;
	private AuthJwtToken authJwtToken;

	public UserAuthentication(AuthJwtToken authJwtToken) {
		this.authJwtToken = authJwtToken;
	}

	@Override
	public String getName() {
		return authJwtToken.getFullName();
	}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> grantedAuthorities = emptyIfNull(authJwtToken.getUserPermissions()).stream()
                .map(SimpleGrantedAuthority::new).collect(toSet());
        if (Objects.nonNull(authJwtToken.getUserTypeKey())) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authJwtToken.getUserTypeKey().name()));
        }

        if (Objects.nonNull(authJwtToken.getWorkerId())){
            grantedAuthorities.add(new SimpleGrantedAuthority("worker"));
        }
        return grantedAuthorities;
    }

	@Override
	public Integer getCredentials() {
        return this.getUserId();
	}

	@Override
	public AuthJwtToken getDetails() {
		return authJwtToken;
	}

	@Override
	public Integer getPrincipal() {
		return this.getUserId();
	}

	public Integer getUserId() {
        return Objects.nonNull(authJwtToken.getUserId()) ? authJwtToken.getUserId() : authJwtToken.getWorkerId();
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