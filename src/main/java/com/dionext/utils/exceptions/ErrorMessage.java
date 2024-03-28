package com.dionext.utils.exceptions;

import java.util.Date;

public class ErrorMessage {
    private final int statusCode;
    private final Date timestamp;
    private final String message;
    private final String description;

    private StackTraceElement[] stackTraceArray;

    private String stackTrace;

    public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public StackTraceElement[] getStackTraceArray() {
        return stackTraceArray;
    }

    public void setStackTraceArray(StackTraceElement[] stackTraceArray) {
        this.stackTraceArray = stackTraceArray;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}