package com.peter.tanxuanfood.exception;

import org.springframework.security.core.AuthenticationException;

public class Unauthenticated extends AuthenticationException {
    public Unauthenticated(String message) {
        super(message);
    }
}
