package com.hathor.docs.auth;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.ardas.jwt.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@CommonsLog
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final Set<String> excludeUrlPatterns;

	public JwtAuthFilter(JwtService jwtService, Set<String> excludeUrlPatterns) {
		this.jwtService = jwtService;
		this.excludeUrlPatterns = excludeUrlPatterns;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		AntPathMatcher pathMatcher = new AntPathMatcher();
		return excludeUrlPatterns.stream()
				.anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(jwtService.getAuthToken(request)));
		filterChain.doFilter(request, response);
	}
}