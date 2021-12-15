package com.skillbox.javapro21.config.security;


import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


import static org.springframework.util.StringUtils.*;

@Component
public class JwtFilter extends GenericFilterBean {
    public static final String AUTH_KEY = "Authorization";

    private final JwtDecoder jwtDecoder;

    private final UserDetailServiceImpl userDetailServiceImpl;

    public JwtFilter(JwtDecoder jwtDecoder, UserDetailServiceImpl userDetailServiceImpl) {
        this.jwtDecoder = jwtDecoder;
        this.userDetailServiceImpl = userDetailServiceImpl;
    }

    // TODO: 14.12.2021 что стоит перед токеном?
    private String getTokenFromHttpServletRequest(HttpServletRequest request) {
        String token = request.getHeader(AUTH_KEY);
        if (hasText(token)) return StringUtils.removeStart(token, "Bearer").trim();
        return null;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromHttpServletRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtDecoder.validateToken(token)) {
            String userLogin = jwtDecoder.getLoginFromToken(token);
            UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(userLogin);
            if (userDetails != null) {
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
