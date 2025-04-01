package com.example.ewalled.app.refresh_token.repository;

import com.example.ewalled.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepistory extends JpaRepository<RefreshToken, Integer>, JpaSpecificationExecutor<RefreshToken> {
}
