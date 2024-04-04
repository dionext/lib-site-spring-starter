package com.dionext.utils.exceptions;

public class BaseException extends RuntimeException {
    private final String message;
    private final Throwable originalException;

    public BaseException(String message) {
        super(message);
        this.message = message;
        this.originalException = null;
    }
    public BaseException(Throwable cause) {
        super(cause);
        this.message = cause.getMessage();
        this.originalException = cause;
    }
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.originalException = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Throwable getOriginalException() {
        return originalException;
    }

}
