package com.example.ewalled.app.money_logs.controller;

import com.example.ewalled.app.money_logs.service.IMoneyLogsService;
import com.example.ewalled.dto.MoneyLogsDto;
import com.example.ewalled.entity.HttpResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/money-logs")
public class MoneyLogsController {
    @Autowired
    private IMoneyLogsService moneyLogsService;

    @GetMapping(
            value = "/summary",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> getList(
            @ModelAttribute @Valid MoneyLogsDto.Request dto
            ){
        var data = this.moneyLogsService.getList(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data.getData(),
                                        null,
                                        "Berhasil mendapatkan data money logs"
                                )
                );
    }

    @GetMapping(
            value = "/graph",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> getGraph(
            @ModelAttribute @Valid MoneyLogsDto.GraphRequest dto
    ){
        var data = this.moneyLogsService.getGraph(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data.getData(),
                                        null,
                                        "Berhasil mendapatkan data graph"
                                )
                );
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponse> save(
            @RequestBody @Valid MoneyLogsDto.NewLogsRequest dto
    ){
        var data = this.moneyLogsService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        HttpResponse
                                .sendSuccessResponse(
                                        data.getData(),
                                        null,
                                        "Berhasil membuat data money logs baru"
                                )
                );
    }
}
