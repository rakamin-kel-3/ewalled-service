package com.example.ewalled.app.user.service;

import com.example.ewalled.app.refresh_token.repository.RefreshTokenRepistory;
import com.example.ewalled.app.user.repository.UserRepository;
import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.RefreshToken;
import com.example.ewalled.exception.DataAlreadyExistException;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.exception.ForbiddenException;
import com.example.ewalled.core.jwt.JwtUtil;
import com.example.ewalled.util.BcryptUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepistory refreshTokenRepistory;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public ServiceData<User> create(UserDto.Create dto) {
        var isExist = this.userRepository.findOne(Example.of(User
                .builder()
                .email(dto.email())
                .build()));

        if (isExist.isPresent()) {
            throw new DataAlreadyExistException("Email sudah terfadtar");
        }

        var newUser = dto.toUser();
        var data = this.userRepository.save(newUser);

        return ServiceData
                .<User>builder()
                .data(data)
                .build();
    }

    @Override
    public ServiceData<UserDto.LoginResponse> login(UserDto.Login dto) {
        var inputUser = dto.toUser();

        User probe = User
                .builder()
                .email(inputUser.getEmail())
                .build();

        var user = this.userRepository.findOne(Example.of(probe))
                .orElseThrow(() -> new DataNotFoundException("Account not found"));

        if (!BcryptUtil.isMatch(inputUser.getPassword(), user.getPassword())) {
            throw new ForbiddenException("credential is not valid");
        }

        var refreshToken = this.refreshTokenRepistory.findOne(Example.of(
                RefreshToken
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseGet(() -> this.refreshTokenRepistory.save(RefreshToken
                .builder()
                .token(UUID.randomUUID().toString())
                .userId(user.getId())
                .expiryDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .build()));

        var jwtToken = jwtUtil.generateJwtToken(user.getId(), user.getEmail());

        return ServiceData
                .<UserDto.LoginResponse>builder()
                .data(new UserDto.LoginResponse(jwtToken, refreshToken.getToken()))
                .build();
    }

    @Override
    public ServiceData<User> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return ServiceData
                .<User>builder()
                .data(user)
                .build();
    }
}
