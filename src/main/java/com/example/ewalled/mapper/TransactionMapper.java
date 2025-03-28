package com.example.ewalled.mapper;

import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.Transaction;
import com.example.ewalled.entity.User;

import java.util.List;

public interface TransactionMapper {
    List<TransactionDto.Response> toResponseList(List<Transaction> transaction);
}
