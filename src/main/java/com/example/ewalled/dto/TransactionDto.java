package com.example.ewalled.dto;

import com.example.ewalled.entity.Transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class TransactionDto {
        public record Transfer(
                        @NotNull(message = "Penerima tidak boleh kosong") @NotBlank(message = "Penerima tidak boleh kosong") String receipentAccountNo,

                        @NotNull(message = "Amount tidak boleh kosong") @Positive(message = "Amount harus lebih dari 0") int amount,

                        @NotNull(message = "Category tidak boleh kosong") @NotBlank(message = "Category tidak boleh kosong") String category,

                        String notes) {

        }

        public record Topup(
                        @NotNull(message = "Amount tidak boleh kosong") int amount,

                        @NotNull(message = "Payment Method tidak boleh kosong") @NotBlank(message = "Payment Method tidak boleh kosong") String paymentMethod,

                        @NotNull(message = "Category tidak boleh kosong") @NotBlank(message = "Category tidak boleh kosong") String category,

                        String notes) {
        }

        public record Response(String transactionId, LocalDateTime createdAt, String type, String fromto,
                        String description, int amount, String inout, String accountTo, String accountFrom) {
        }
}
