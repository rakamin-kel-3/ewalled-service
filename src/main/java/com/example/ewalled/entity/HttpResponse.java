package com.example.ewalled.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;

@Builder(access = AccessLevel.PRIVATE)
@Setter
@Getter
@Slf4j
public class HttpResponse {

    private Object data;

    private Meta metadata;

    @Builder
    @Setter
    @Getter
    static class Meta {
        private String message;
        private boolean isSuccess;
    }

    public static HttpResponse sendSuccessResponse(
            Object data,
            String message
    ) {
        return HttpResponse
                .builder()
                .metadata(
                        Meta
                                .builder()
                                .message(message)
                                .isSuccess(true)
                                .build()
                )
                .data(data)
                .build();
    }

    public static HttpResponse sendErrorResponse(
            String message
    ) {
        return HttpResponse
                .builder()
                .metadata(
                        Meta
                                .builder()
                                .message(message)
                                .isSuccess(false)
                                .build()
                )
                .build();
    }

}
