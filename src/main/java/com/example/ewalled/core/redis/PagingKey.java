package com.example.ewalled.core.redis;

import org.springframework.data.domain.Pageable;

public record PagingKey(int page, int size, String sort) {
    public static PagingKey from(Pageable pageable) {
        return new PagingKey(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString() // Example: "createdAt: DESC"
        );
    }
}
