package com.ascendcorp.exam.exception;

public class GeneralInvalidDataException extends RuntimeException{

    private final String code;

    public GeneralInvalidDataException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
