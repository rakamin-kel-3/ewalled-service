package com.example.ewalled.entity;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceData<T> {
    private T data;
    private Object metadata;
}
