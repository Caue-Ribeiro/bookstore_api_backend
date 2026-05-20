package com.caue.bookstore.controllers.exceptionHandler;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError{

    List<FieldError> fieldErrors = new ArrayList<>();

    public ValidationError(Instant timestamp, HttpStatus status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public void addError(String field, String message){

        fieldErrors.add(new FieldError(field, message));
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}
