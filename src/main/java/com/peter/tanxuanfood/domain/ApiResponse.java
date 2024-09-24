package com.peter.tanxuanfood.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T> {
    private int statusCode;
    private Object message;
    private String error;
    private T data;
}
