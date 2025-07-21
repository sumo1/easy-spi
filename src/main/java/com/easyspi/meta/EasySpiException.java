package com.easyspi.meta;

public class EasySpiException extends RuntimeException {

    public EasySpiException(String errorMsg) {
        this(errorMsg, null);
    }

    public EasySpiException(String errorMsg, Throwable throwable) {
        super(errorMsg, throwable);
    }
}
