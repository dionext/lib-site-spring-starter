package com.dionext.utils.exceptions;

public class ResourceFindException extends BaseException {
    public ResourceFindException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceFindException(Throwable cause) {
        super(cause);
    }

    public ResourceFindException(String message) {
        super(message);
    }

}
