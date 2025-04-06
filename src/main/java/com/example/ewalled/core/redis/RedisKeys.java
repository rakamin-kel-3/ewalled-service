package com.example.ewalled.core.redis;

public class RedisKeys {
    // transaction
    public static final String TRANSACTION_GETLIST_KEY = "fulusku:transaction:getList:user:%s:pg:%s";
    public static final String TRANSACTION_GETLIST_PATTERN = "fulusku:transaction:getList:user:%s:*";

    // money logs
    public static final String MONEYLOGS_GETLIST_KEY = "fulusku:moneylogs:%s:user:%s:q:%s";
    public static final String MONEYLOGS_GETLIST_PATTERN = "fulusku:moneylogs:*:user:%s:*";
}