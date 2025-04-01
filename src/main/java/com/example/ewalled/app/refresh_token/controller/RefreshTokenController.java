package com.example.ewalled.app.refresh_token.controller;

import com.example.ewalled.app.refresh_token.service.RefreshTokenService;
import com.example.ewalled.dto.RefreshTokenDto;
import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.HttpResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefreshTokenController {
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping(
            value = "/auth/refresh-token",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> refresh(
            @RequestBody @Valid RefreshTokenDto.Refresh dto
            ){
        var data = this.refreshTokenService.save(dto).getData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data,
                                        null,
                                        "Berhasil refresh token"
                                )
                );
    }
}
