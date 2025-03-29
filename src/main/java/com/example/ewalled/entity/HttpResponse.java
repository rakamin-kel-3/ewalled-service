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
    private Pagination pagination;

    @Builder
    @Setter
    @Getter
    static class Meta {
        private String message;
        private boolean isSuccess;
    }

    @Builder
    @Setter
    @Getter
    public static class Pagination {
        private Long totalItems;
        private int currentPage;
        private int totalPages;
        private int pageSize;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    public static HttpResponse sendSuccessResponse(
            Object data,
            Pagination pagination,
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
                .pagination(pagination)
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
