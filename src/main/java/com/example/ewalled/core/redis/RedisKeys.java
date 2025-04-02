package com.example.ewalled.core.redis;

public class RedisKeys {

    // account
    public static final String ACCOUNT_PATTERN = "fulusku:account:*";
    public static final String ACCOUNT_GET_KEY = "fulusku:account:get:%s";
    public static final String ACCOUNT_GETLIST_KEY = "fulusku:account:getList:%s";

    // transaction
    public static final String TRANSACTION_GETLIST_KEY = "fulusku:transaction:getList:user:%s:pg:%s";
    public static final String TRANSACTION_GETLIST_PATTERN = "fulusku:transaction:getList:user:%s:*";

}