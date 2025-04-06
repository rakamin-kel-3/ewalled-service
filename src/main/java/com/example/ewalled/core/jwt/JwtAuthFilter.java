package com.example.ewalled.core.jwt;

import com.example.ewalled.app.user.repository.UserRepository;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.ForbiddenException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            if (!token.isEmpty()) {
                try {
                    Claims claims = jwtUtil.getTokenData(token);

                    if (claims.get("id") != null) {
                        var user = this.userRepository.findOne(Example.of(User
                                .builder()
                                .id((Integer) claims.get("id"))
                                .build()));

                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.get(),
                                null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }

                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    // throw error when token expired
                    resolver.resolveException(request, response, null, new ForbiddenException("Token Expired"));
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
