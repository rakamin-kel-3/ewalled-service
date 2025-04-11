package com.example.ewalled.app.refresh_token.service;

import com.example.ewalled.app.refresh_token.repository.RefreshTokenRepistory;
import com.example.ewalled.app.user.repository.UserRepository;
import com.example.ewalled.dto.RefreshTokenDto;
import com.example.ewalled.entity.RefreshToken;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.core.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    private RefreshTokenRepistory refreshTokenRepistory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ServiceData<RefreshTokenDto.Response> save(RefreshTokenDto.Refresh dto) {
        RefreshToken token = this.refreshTokenRepistory.findOne(Example.of(
                RefreshToken
                        .builder()
                        .token(dto.refreshToken())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("refresh token is invalid"));

        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));
        this.refreshTokenRepistory.save(token);

        var user = this.userRepository.findById(token.getUserId())
                .orElseThrow(() -> new DataNotFoundException("user not found"));

        var jwtToken = jwtUtil.generateJwtToken(user.getId(), user.getEmail());

        return ServiceData
                .<RefreshTokenDto.Response>builder()
                .data(new RefreshTokenDto.Response(jwtToken, token.getToken()))
                .build();
    }
}
