package com.sekou.fullstack.exception;

public class OperationNotPermittedException extends RuntimeException{
    public OperationNotPermittedException(String message) {
        super(message);
    }

}
