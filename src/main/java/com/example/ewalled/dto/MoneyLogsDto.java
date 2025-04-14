package com.example.ewalled.dto;

import com.example.ewalled.annotation.ValidEnum;
import com.example.ewalled.domain.enums.GraphType;
import com.example.ewalled.entity.MoneyLogs;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MoneyLogsDto {
    public record Request(
            @NotNull(message = "Start Date tidak boleh kosong")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
            LocalDate startDate,

            @NotNull(message = "End Date tidak boleh kosong")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
            LocalDate endDate
    ) {}

    public record GraphRequest(
            @NotNull(message = "Type tidak boleh kosong")
            @NotBlank(message = "Type tidak boleh kosong")
            @ValidEnum(enumClass = GraphType.class, message = "Type harus income atau expense")
            String type,

            @NotNull(message = "Start Date tidak boleh kosong")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
            LocalDate startDate,

            @NotNull(message = "End Date tidak boleh kosong")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
            LocalDate endDate
    ) {}

    public record NewLogsRequest(
            @NotNull(message = "Amount tidak boleh kosong")
            @Positive(message = "Amount harus lebih dari 0")
            Integer amount,

            @NotNull(message = "Category tidak boleh kosong")
            @NotBlank(message = "Category tidak boleh kosong")
            String category,

            @NotNull(message = "Type tidak boleh kosong")
            @NotBlank(message = "Type tidak boleh kosong")
            @ValidEnum(enumClass = GraphType.class, message = "Type harus income atau expense")
            String type,

            @NotNull(message = "Date tidak boleh kosong")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate date,

            String notes
    ) {
        public MoneyLogs toEntity() {
            return MoneyLogs
                    .builder()
                    .category(this.category)
                    .amount(this.amount)
                    .type(this.type)
                    .date(this.date)
                    .notes(this.notes)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }
    }

    @Builder
    public record GetListResponse(Integer income, Integer expense, LocalDate periodStart, LocalDate periodEnd, List<MoneyLogs> data) {}

    @Builder
    public record GraphItem(double percentage, String category, Integer amount) {}

    @Builder
    public record GetGraphResponse(String type, Integer totalAmount, List<GraphItem> items) {}
}
