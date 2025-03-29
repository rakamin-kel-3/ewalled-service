package com.example.ewalled.app.transaction.controller;

import com.example.ewalled.app.transaction.service.TransactionService;
import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.HttpResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> getList(Pageable pageable){
        var data = this.transactionService.getList(pageable);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data.getData(),
                                        data.getPagination(),
                                        "Berhasil mendapatkan data transactions"
                                )
                );
    }

    @PostMapping(
            value = "/transfer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> transfer(
            @RequestBody @Valid TransactionDto.Transfer dto
    ){
        var data = this.transactionService.transfer(dto).getData();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data,
                                        null,
                                        "Berhasil melakukan transfer"
                                )
                );
    }

    @PostMapping(
            value = "/topup",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> topup(
            @RequestBody @Valid TransactionDto.Topup dto
    ){
        var data = this.transactionService.topup(dto).getData();
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data,
                                        null,
                                        "Berhasil melakukan topup"
                                )
                );
    }
}
