package com.caue.bookstore.controllers.exceptionHandler;


import com.caue.bookstore.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_CONTENT;

        ValidationError error = new ValidationError(Instant.now(), httpStatus, "Invalid data", request.getRequestURI());

        List<FieldError> fieldError = e.getBindingResult().getFieldErrors();

        fieldError.forEach(fieldError1 -> error.addError(fieldError1.getField(), fieldError1.getDefaultMessage()));

        return ResponseEntity.status(httpStatus).body(error);

    }
}
