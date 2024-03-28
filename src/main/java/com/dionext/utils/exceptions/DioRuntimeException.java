package com.dionext.utils.exceptions;

public class DioRuntimeException extends RuntimeException {
    public DioRuntimeException() {
    }

    public DioRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DioRuntimeException(Throwable cause) {
        super(cause);
    }

    public DioRuntimeException(String message) {
        super(message);
    }
}
