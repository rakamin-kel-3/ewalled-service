package com.example.ewalled.app.transaction.service;

import com.example.ewalled.app.account.repository.AccountRepository;
import com.example.ewalled.app.money_logs.repository.MoneyLogsRepository;
import com.example.ewalled.app.transaction.repository.TransactionRepository;
import com.example.ewalled.core.redis.CacheKeyBuilder;
import com.example.ewalled.core.redis.PagingKey;
import com.example.ewalled.core.redis.RedisCacheService;
import com.example.ewalled.core.redis.RedisKeys;
import com.example.ewalled.dto.TransactionDto;
import com.example.ewalled.entity.*;
import com.example.ewalled.exception.DataNotFoundException;
import com.example.ewalled.exception.InsufficientBalanceException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TransactionService implements ITransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MoneyLogsRepository moneyLogsRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    @Override
    public ServiceData<List<TransactionDto.Response>> getList(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        String key = String.format(RedisKeys.TRANSACTION_GETLIST_KEY, user.getId(),CacheKeyBuilder.buildFrom(PagingKey.from(pageable)));
        ServiceData<List<TransactionDto.Response>> cached = this.redisCacheService.get(key, new TypeReference<>() {
        });
        if (cached != null) {
            log.info("Get from redis key : {}", key);
            return cached;
        }

        var account = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("account tidak ditemukan"));

        var transactions = this.transactionRepository.findBySenderAccountIdOrReceipentAccountId(account.getId(), account.getId(), pageable);

        if (transactions.getContent().isEmpty()) {
            throw new DataNotFoundException("transaction not found");
        }

        Map<Integer, String> accountNameMap = new HashMap<>();
        for (Transaction t : transactions.getContent()) {
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

        for (Transaction t : transactions.getContent()) {
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
                    "",
                    t.getCategory()
            ));
        }

        var res = ServiceData
                .<List<TransactionDto.Response>>builder()
                .data(response)
                .pagination(HttpResponse.Pagination.builder()
                        .totalItems(transactions.getTotalElements())
                        .currentPage(transactions.getNumber())
                        .totalPages(transactions.getTotalPages())
                        .pageSize(transactions.getTotalPages())
                        .hasNext(transactions.hasNext())
                        .hasPrevious(transactions.hasPrevious())
                        .build())
                .build();

        this.redisCacheService.setEX(key, res, Duration.ofMinutes(5));
        log.info("Set new redis key : {}", key);

        return res;
    }

    @Override
    @Transactional
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
                        .category(dto.category())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build());

        this.moneyLogsRepository.save(MoneyLogs
                .builder()
                .date(LocalDate.now())
                .isTransaction(true)
                .transactionId(trx.getId())
                .amount(dto.amount())
                .category(dto.category())
                .notes(dto.notes())
                .userId(user.getId())
                .type("expense")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        this.moneyLogsRepository.save(MoneyLogs
                .builder()
                .date(LocalDate.now())
                .isTransaction(true)
                .transactionId(trx.getId())
                .amount(dto.amount())
                .category("transfer")
                .notes(dto.notes())
                .userId(receipentAccount.getUserId())
                .type("income")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        String key = String.format(RedisKeys.TRANSACTION_GETLIST_PATTERN, myAccount.getUserId());
        this.redisCacheService.delete(key);

        key = String.format(RedisKeys.MONEYLOGS_GETLIST_PATTERN, myAccount.getUserId());
        this.redisCacheService.delete(key);


        key = String.format(RedisKeys.TRANSACTION_GETLIST_PATTERN, receipentAccount.getUserId());
        this.redisCacheService.delete(key);

        key = String.format(RedisKeys.MONEYLOGS_GETLIST_PATTERN, receipentAccount.getUserId());
        this.redisCacheService.delete(key);

        return ServiceData
                .<TransactionDto.Response>builder()
                .data(
                        new TransactionDto.Response(trx.getTransactionId(), trx.getCreatedAt(), trx.getTypeTrx(), "", trx.getDescription(), trx.getAmount(), "out", receipentAccount.getAccountNo(), myAccount.getAccountNo(), trx.getCategory())
                ).build();
    }

    private String generateTransactionId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    @Transactional
    public ServiceData<TransactionDto.Response> topup(TransactionDto.Topup dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var myAccount = this.accountRepository.findOne(Example.of(
                Account
                        .builder()
                        .userId(user.getId())
                        .build()
        )).orElseThrow(() -> new DataNotFoundException("Terjadi kesalahan"));

        myAccount.setBalance(myAccount.getBalance() + dto.amount());
        this.accountRepository.save(myAccount);

        var trx = this.transactionRepository.save(Transaction
                .builder()
                .transactionId(this.generateTransactionId())
                .amount(dto.amount())
                .status("settled")
                .typeTrx("topup")
                .category(dto.paymentMethod())
                .receipentAccountId(myAccount.getId())
                .description(dto.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        this.moneyLogsRepository.save(MoneyLogs
                .builder()
                .date(LocalDate.now())
                .isTransaction(true)
                .transactionId(trx.getId())
                .amount(dto.amount())
                .category(dto.paymentMethod())
                .notes(dto.notes())
                .userId(user.getId())
                .type("income")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );

        String key = String.format(RedisKeys.TRANSACTION_GETLIST_PATTERN, myAccount.getUserId());
        this.redisCacheService.delete(key);

        key = String.format(RedisKeys.MONEYLOGS_GETLIST_PATTERN, myAccount.getUserId());
        this.redisCacheService.delete(key);

        return ServiceData
                .<TransactionDto.Response>builder()
                .data(
                        new TransactionDto.Response(trx.getTransactionId(), trx.getCreatedAt(), trx.getTypeTrx(), "", trx.getDescription(), trx.getAmount(), "out", "", "", trx.getCategory())
                ).build();
    }
}
