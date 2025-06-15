package com.ascendcorp.exam.exception;

public class InternalServerErrorException extends RuntimeException{

    private final String code;

    public InternalServerErrorException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
