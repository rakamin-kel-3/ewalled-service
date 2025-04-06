package com.example.ewalled.app.money_logs.repository;

import com.example.ewalled.entity.MoneyLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MoneyLogsRepository extends JpaRepository<MoneyLogs, Integer> {
    List<MoneyLogs> findByUserIdAndDateBetweenOrderByDateDesc(Integer userId, LocalDate start, LocalDate end);
    List<MoneyLogs> findByUserIdAndTypeAndDateBetween(Integer userId, String type, LocalDate start, LocalDate end);
}
