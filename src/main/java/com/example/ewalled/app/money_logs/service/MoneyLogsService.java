package com.example.ewalled.app.money_logs.service;

import com.example.ewalled.app.money_logs.repository.MoneyLogsRepository;
import com.example.ewalled.app.transaction.repository.TransactionRepository;
import com.example.ewalled.dto.MoneyLogsDto;
import com.example.ewalled.entity.MoneyLogs;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.User;
import com.example.ewalled.exception.BackdateException;
import com.example.ewalled.exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class MoneyLogsService implements IMoneyLogsService{

    @Autowired
    private MoneyLogsRepository moneyLogsRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public ServiceData<MoneyLogsDto.GetListResponse> getList(MoneyLogsDto.Request dto) {
        if (dto.endDate().isBefore(dto.startDate())) {
            throw new BackdateException("End date is backdate with Start Date");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var moneyLogs = this.moneyLogsRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), dto.startDate(), dto.endDate());

        if (moneyLogs.isEmpty()) {
            throw new DataNotFoundException("Data tidak ditemukan");
        }

        var income = 0;
        var expense = 0;

        for (MoneyLogs m :
                moneyLogs) {
            if (m.getType().equals("income")) {
                income += m.getAmount();
                continue;
            }
            expense += m.getAmount();
        }

        return ServiceData
                .<MoneyLogsDto.GetListResponse>builder()
                .data(MoneyLogsDto.GetListResponse
                        .builder()
                        .data(moneyLogs)
                        .income(income)
                        .expense(expense)
                        .periodStart(dto.startDate())
                        .periodEnd(dto.endDate())
                        .build())
                .build();
    }

    @Override
    public ServiceData<MoneyLogsDto.GetGraphResponse> getGraph(MoneyLogsDto.GraphRequest dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        var moneyLogs = this.moneyLogsRepository.findByUserIdAndTypeAndDateBetweenOrderByDateDesc(user.getId(), dto.type(), dto.startDate(), dto.endDate());

        if (moneyLogs.isEmpty()) {
            throw new DataNotFoundException("Data tidak ditemukan");
        }

        int totalAmount = 0;
        HashMap<String, MoneyLogsDto.GraphItem> categoryMap = new HashMap<>();

        for (MoneyLogs m : moneyLogs) {
            if (!categoryMap.containsKey(m.getCategory())) {
                categoryMap.put(m.getCategory(), MoneyLogsDto.GraphItem
                        .builder()
                        .category(m.getCategory())
                        .amount(m.getAmount())
                        .percentage(0)
                        .build());
                totalAmount += m.getAmount();
                continue;
            }
            var tempGraph = categoryMap.get(m.getCategory());
            categoryMap.replace(m.getCategory(), MoneyLogsDto.GraphItem
                    .builder()
                    .category(m.getCategory())
                    .amount(m.getAmount()+ tempGraph.amount())
                    .percentage(0)
                    .build());
            totalAmount += m.getAmount();
        }

        List<MoneyLogsDto.GraphItem> graphList = new ArrayList<>();

        int finalTotalAmount = totalAmount;
        categoryMap.forEach((k, v) -> {
            double percentage = ((double) v.amount() / finalTotalAmount) * 100;
            graphList.add(MoneyLogsDto.GraphItem
                    .builder()
                    .category(v.category())
                    .amount(v.amount())
                    .percentage(Math.round(percentage))
                    .build());
        });

        return ServiceData
                .<MoneyLogsDto.GetGraphResponse>builder()
                .data(MoneyLogsDto.GetGraphResponse
                        .builder()
                        .totalAmount(finalTotalAmount)
                        .items(graphList)
                        .type(dto.type())
                        .build())
                .build();
    }
}
