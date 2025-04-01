package com.example.ewalled.app.user.controller;

import com.example.ewalled.app.account.service.IAccountService;
import com.example.ewalled.app.user.service.IUserService;
import com.example.ewalled.dto.AccountDto;
import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.HttpResponse;
import com.example.ewalled.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Autowired
    private IUserService userService;

    @Autowired
    private IAccountService accountService;

    @PostMapping(
            value = "/auth/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> save(
            @RequestBody @Valid UserDto.Create dto
    ){
        var data = this.userService.create(dto).getData();
        this.accountService.save(new AccountDto.Create(data.getId(), data.getName()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        userMapper.toResponse(data),
                                        null,
                                        "Berhasil register user"
                                )
                );
    }

    @PostMapping(
            value = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> login(
            @RequestBody @Valid UserDto.Login dto
    ){
        var data = this.userService.login(dto).getData();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data,
                                        null,
                                        "Berhasil login"
                                )
                );
    }

    @GetMapping(
            value = "/users/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> me(
    ){
        var data = this.userService.me().getData();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        userMapper.toResponse(data),
                                        null,
                                        "Berhasil mendapatkan data profile"
                                )
                );
    }

}
