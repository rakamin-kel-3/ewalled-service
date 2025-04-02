package com.example.ewalled.core.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.digest.DigestUtils;

public class CacheKeyBuilder {

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

    public static String buildFrom(Object probe) {
        try {
            return mapper.writeValueAsString(probe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize cache key", e);
        }
    }
}
