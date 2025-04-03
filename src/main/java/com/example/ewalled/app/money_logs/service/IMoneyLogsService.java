package com.example.ewalled.app.money_logs.service;

import com.example.ewalled.dto.MoneyLogsDto;
import com.example.ewalled.entity.ServiceData;

public interface IMoneyLogsService {
    ServiceData<MoneyLogsDto.GetListResponse> getList(MoneyLogsDto.Request dto);
    ServiceData<MoneyLogsDto.GetGraphResponse> getGraph(MoneyLogsDto.GraphRequest dto);
}
