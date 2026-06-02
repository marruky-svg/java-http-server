package com.marruky.Http;

public class CircuitBreakerOpenException extends RuntimeException{
    public CircuitBreakerOpenException(String errorMsg){
        super(errorMsg);
    }
}
