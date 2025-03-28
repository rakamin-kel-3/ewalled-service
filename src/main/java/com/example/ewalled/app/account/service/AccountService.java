package com.example.ewalled.app.account.service;

import com.example.ewalled.app.account.repository.AccountRepository;
import com.example.ewalled.dto.AccountDto;
import com.example.ewalled.entity.Account;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.DataAlreadyExistException;
import com.example.ewalled.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AccountService implements IAccountService{

    private static final int MAX_RETRIES = 5;
    private static final String PREFIX = "100";
    private final Random random = new Random();

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ServiceData<Account> save(AccountDto.Create dto) {
        var accInput = dto.toAccount();
        Account probe = Account
                .builder()
                .userId(accInput.getUserId())
                .build();
        var isExist = this.accountRepository.findOne(Example.of(probe));

        if (isExist.isPresent()) {
            throw new DataAlreadyExistException("User already have an account");
        }

        boolean isSuccess = false;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String candidate = this.generateAccountNo();
            accInput.setAccountNo(candidate);
            try {
                this.accountRepository.save(accInput);
                isSuccess = true;
                break;
            } catch (DataIntegrityViolationException ex) {
                log.info(ex.getMessage());
            }
        }

        if (!isSuccess) {
            throw new RuntimeException("failed to create account");
        }

        return ServiceData
                .<Account>builder()
                .data(accInput)
                .build();
    }

    private String generateAccountNo() {
        return PREFIX + String.format("%06d", random.nextInt(1_000_000));
    }

    @Override
    public ServiceData<Account> get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Account probe = Account
                .builder()
                .userId(user.getId())
                .build();

        var account = this.accountRepository.findOne(Example.of(probe))
                .orElseThrow(() -> new DataNotFoundException("Account not found"));

        return ServiceData
                .<Account>builder()
                .data(account)
                .build();
    }

    @Override
    public ServiceData<List<AccountDto.AccountTransferResponse>> getListForTransfer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var accounts = this.accountRepository.findAll((root, query, cb) ->
                cb.notEqual(root.get("userId"), user.getId()));

        List<AccountDto.AccountTransferResponse> response = accounts.stream()
                .map(account -> new AccountDto.AccountTransferResponse(account.getName(), account.getAccountNo()))
                .toList();

        return ServiceData
                .<List<AccountDto.AccountTransferResponse>>builder()
                .data(response)
                .build();
    }
}
