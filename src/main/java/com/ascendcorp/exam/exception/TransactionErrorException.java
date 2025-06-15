package com.ascendcorp.exam.exception;

public class TransactionErrorException extends RuntimeException{

    private final String code;

    public TransactionErrorException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
