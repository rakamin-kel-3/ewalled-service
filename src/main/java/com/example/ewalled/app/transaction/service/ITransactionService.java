package com.example.ewalled.app.transaction.service;

import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.Transaction;

import java.util.List;

public interface ITransactionService {
    ServiceData<List<TransactionDto.Response>> get();
    ServiceData<TransactionDto.Response> transfer(TransactionDto.Transfer dto);

    ServiceData<TransactionDto.Response> topup(TransactionDto.Topup dto);
}
