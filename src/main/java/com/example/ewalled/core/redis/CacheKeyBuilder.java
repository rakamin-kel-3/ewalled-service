package com.example.ewalled.core.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CacheKeyBuilder {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String buildFrom(Object probe) {
        try {
            return mapper.writeValueAsString(probe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize cache key", e);
        }
    }
}
