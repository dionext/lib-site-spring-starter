package com.dionext.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
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
