package com.example.ewalled.dto;

import com.example.ewalled.entity.User;
import com.example.ewalled.util.BcryptUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {

    public record Create(
            @NotNull(message = "Nama tidak boleh kosong")
            @NotBlank(message = "Nama tidak boleh kosong")
            String name,

            @NotNull(message = "Email tidak boleh kosong")
            @NotBlank(message = "Email tidak boleh kosong")
            String email,

            @NotNull(message = "Password tidak boleh kosong")
            @NotBlank(message = "Password tidak boleh kosong")
            String password,

            @NotNull(message = "No HP tidak boleh kosong")
            @NotBlank(message = "No HP tidak boleh kosong")
            String phoneNumber
    ) {
        public User toUser() {
            return User
                    .builder()
                    .name(this.name)
                    .email(this.email)
                    .phoneNumber(this.phoneNumber)
                    .password(BcryptUtil.encode(this.password))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }

    public record Login(
            @NotNull(message = "Email tidak boleh kosong")
            @NotBlank(message = "Email tidak boleh kosong")
            String email,

            @NotNull(message = "Password tidak boleh kosong")
            @NotBlank(message = "Password tidak boleh kosong")
            String password
    ) {
        public User toUser() {
            return User
                    .builder()
                    .email(this.email)
                    .password(this.password)
                    .build();
        }
    }

    public record Response(Integer id, String username, String email, String name, String phoneNumber, String avatar) {}
}
