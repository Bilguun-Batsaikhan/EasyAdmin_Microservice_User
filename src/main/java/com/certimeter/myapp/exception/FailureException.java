package com.certimeter.myapp.exception;

import com.certimeter.myapp.enumeration.ResponseEnum;

public class FailureException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ResponseEnum responseEnum;
    private Exception exception;

    public FailureException(ResponseEnum responseEnum) {
        super();
        this.responseEnum = responseEnum;
    }

    public FailureException(ResponseEnum responseEnum, Exception exception) {
        super();
        this.responseEnum = responseEnum;
        this.exception = exception;
    }

    public ResponseEnum getResponseEnum() {
        return responseEnum;
    }

    public Exception getException() {
        return exception;
    }
}
