package com.example.ewalled.dto;

import com.example.ewalled.entity.RefreshToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RefreshTokenDto {

    public record Refresh(
            @NotNull(message = "Refresh Token cant be null")
            @NotBlank(message =  "Refresh Token cant be blank")
            String refreshToken
    ) {
        public RefreshToken toEntity() {
            return RefreshToken
                    .builder()
                    .token(this.refreshToken)
                    .build();
        }
    }

    public record Response(String token, String refreshToken) {}
}
