package com.example.ewalled.app.transaction.service;

import com.example.ewalled.app.account.repository.AccountRepository;
import com.example.ewalled.app.transaction.repository.TransactionRepository;
import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.entity.Account;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.Transaction;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.exception.InsufficientBalanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TransactionService implements ITransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ServiceData<List<TransactionDto.Response>> get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var account = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("account tidak ditemukan"));

        var transactions = this.transactionRepository.findBySenderAccountIdOrReceipentAccountIdOrderByCreatedAtDesc(account.getId(), account.getId())
                .orElseThrow(() -> new DataNotFoundException("Transaction tidak ditemukan"));

        Map<Integer, String> accountNameMap = new HashMap<>();
        for (Transaction t : transactions) {
            if (!accountNameMap.containsKey(t.getReceipentAccountId()) && t.getReceipentAccountId() != account.getId()) {
                accountNameMap.put(t.getReceipentAccountId(), "");
                continue;
            }
            if (!accountNameMap.containsKey(t.getSenderAccountId()) && t.getSenderAccountId() != account.getId()) {
                accountNameMap.put(t.getSenderAccountId(), "");
            }
        }

        var accounts = this.accountRepository.findByIdIn(accountNameMap.keySet().stream().toList());

        for (Account a : accounts) {
            accountNameMap.put(a.getId(), a.getName());
        }

        List<TransactionDto.Response> response = new ArrayList<>();

        for (Transaction t : transactions) {
            String fromto = "";
            String inout = "in";
            if (t.getTypeTrx().equals("transfer")) {
                if (t.getReceipentAccountId() != account.getId()) {
                    fromto = accountNameMap.get(t.getReceipentAccountId());
                    inout = "out";
                } else {
                    fromto = accountNameMap.get(t.getSenderAccountId());
                }
            }
            response.add(new TransactionDto.Response(
                    t.getTransactionId(),
                    t.getCreatedAt(),
                    t.getTypeTrx(),
                    fromto,
                    t.getDescription(),
                    t.getAmount(),
                    inout,
                    "",
                    ""
            ));
        }

        return ServiceData
                .<List<TransactionDto.Response>>builder()
                .data(response)
                .build();
    }

    @Override
    public ServiceData<TransactionDto.Response> transfer(TransactionDto.Transfer dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var myAccount = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("Terjadi kesalahan"));

        if (myAccount.getBalance() < dto.amount()) {
            throw new InsufficientBalanceException("Balance tidak mencukupi");
        }

        var receipentAccount = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .accountNo(dto.receipentAccountNo())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("Account tujuan tidak ditemukan"));

        myAccount.setBalance(myAccount.getBalance() - dto.amount());
        this.accountRepository.save(myAccount);

        receipentAccount.setBalance(receipentAccount.getBalance() + dto.amount());
        this.accountRepository.save(receipentAccount);

        var trx = this.transactionRepository.save(Transaction
                        .builder()
                        .transactionId(this.generateTransactionId())
                        .amount(dto.amount())
                        .senderAccountId(myAccount.getId())
                        .receipentAccountId(receipentAccount.getId())
                        .status("settled")
                        .typeTrx("transfer")
                        .description(dto.notes())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        return ServiceData
                .<TransactionDto.Response>builder()
                .data(
                        new TransactionDto.Response(trx.getTransactionId(), trx.getCreatedAt(), trx.getTypeTrx(), "", trx.getDescription(), trx.getAmount(), "out", receipentAccount.getAccountNo(), myAccount.getAccountNo())
                ).build();
    }

    private String generateTransactionId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    public ServiceData<TransactionDto.Response> topup(TransactionDto.Topup dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var myAccount = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("Terjadi kesalahan"));

        if (myAccount.getBalance() < dto.amount()) {
            throw new InsufficientBalanceException("Balance tidak mencukupi");
        }

        myAccount.setBalance(myAccount.getBalance() + dto.amount());
        this.accountRepository.save(myAccount);

        var trx = this.transactionRepository.save(Transaction
                .builder()
                .transactionId(this.generateTransactionId())
                .amount(dto.amount())
                .status("settled")
                .typeTrx("topup")
                .receipentAccountId(myAccount.getId())
                .description((dto.notes() == null ? "Top Up from " + dto.paymentMethod() : dto.notes()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        return ServiceData
                .<TransactionDto.Response>builder()
                .data(
                        new TransactionDto.Response(trx.getTransactionId(), trx.getCreatedAt(), trx.getTypeTrx(), "", trx.getDescription(), trx.getAmount(), "out", "", "")
                ).build();
    }
}
