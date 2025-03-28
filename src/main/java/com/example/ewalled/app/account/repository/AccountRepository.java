package com.example.ewalled.app.account.repository;

import com.example.ewalled.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer>, JpaSpecificationExecutor<Account> {
    List<Account> findByIdIn(List<Integer> ids);
}
