package com.peter.tanxuanfood.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    private int statusCode;
    private Object message;
    private String error;
    private T data;
}
