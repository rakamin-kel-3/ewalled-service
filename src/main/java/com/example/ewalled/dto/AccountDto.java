package com.example.ewalled.dto;

import com.example.ewalled.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AccountDto {

    public record Create(
            @NotNull(message = "User Id tidak boleh kosong")
            @NotBlank(message = "User Id tidak boleh kosong")
            int userId,

            @NotNull(message = "Name tidak boleh kosong")
            @NotBlank(message = "Name tidak boleh kosong")
            String name
    ) {
        public Account toAccount() {
            return Account
                    .builder()
                    .userId(this.userId)
                    .name(this.name)
                    .balance(0)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }

    public record AccountTransferResponse(String name, String accountNo) {}

}
