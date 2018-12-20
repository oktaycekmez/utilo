package com.utilo.core.exception;

public class IllegalParameterException extends BaseException {
    public IllegalParameterException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public IllegalParameterException(String exceptionMessage, Exception e) {
        super(exceptionMessage, e);
    }
}
