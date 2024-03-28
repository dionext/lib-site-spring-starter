package com.dionext.utils.exceptions;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest webRequest, HttpServletRequest request) {
        HttpStatusCode status = HttpStatus.INTERNAL_SERVER_ERROR;
        String contextPath = "";
        if (request != null) {
            request.getContextPath();// ""
            contextPath = "Context path: " + request.getRequestURI() + " ";
        }
        String message = ex.toString();
        if (ex instanceof ResourceFindException
                || ex instanceof org.springframework.web.servlet.NoHandlerFoundException
                || ex instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            log.error(contextPath + "ExceptionHandler Error " + message);
            status = HttpStatus.NOT_FOUND;
        } else {
            log.error(contextPath + "ExceptionHandler Error " + message, ex);
        }
        return new ResponseEntity<>(message, status);
    }

}
