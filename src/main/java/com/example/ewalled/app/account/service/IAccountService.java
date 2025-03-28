package com.example.ewalled.app.account.service;

import com.example.ewalled.dto.AccountDto;
import com.example.ewalled.entity.Account;
import com.example.ewalled.entity.ServiceData;

import java.util.List;

public interface IAccountService {
    ServiceData<Account> save(AccountDto.Create dto);
    ServiceData<Account> get();

    ServiceData<List<AccountDto.AccountTransferResponse>> getListForTransfer();
}
