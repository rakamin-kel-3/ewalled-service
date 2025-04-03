package com.example.ewalled.dto;

import com.example.ewalled.entity.MoneyLogs;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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
    @Builder
    public record GetListResponse(Integer income, Integer expense, LocalDate periodStart, LocalDate periodEnd, List<MoneyLogs> data) {}
}
