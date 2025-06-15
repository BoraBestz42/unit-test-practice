package com.ascendcorp.exam.exception;

public class WebServerErrorException extends RuntimeException{

    private final String code;

    public WebServerErrorException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
