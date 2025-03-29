package com.example.ewalled.app.transaction.service;

import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.Transaction;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITransactionService {
    ServiceData<List<TransactionDto.Response>> getList(Pageable pageable);
    ServiceData<TransactionDto.Response> transfer(TransactionDto.Transfer dto);

    ServiceData<TransactionDto.Response> topup(TransactionDto.Topup dto);
}
