package com.caue.bookstore.controllers.exceptionHandler;


import com.caue.bookstore.exceptions.*;
import io.jsonwebtoken.ExpiredJwtException;
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

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<CustomError> dataIntegrityViolation(DatabaseException e,
                                                              HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);

    }

    @ExceptionHandler({InsufficientStockException.class, InvalidOrderStateException.class})
    public ResponseEntity<CustomError> orderBusinessRule(RuntimeException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CustomError> expiredJwt(ExpiredJwtException e,
                                                              HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);

    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<CustomError> weakPassword(WeakPasswordException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<CustomError> userLocked(UserLockedException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.LOCKED;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<CustomError> invalidResetToken(InvalidResetTokenException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomError> customException(CustomException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;

        CustomError error = new CustomError(Instant.now(), httpStatus, e.getMessage(), request.getRequestURI());

        return ResponseEntity.status(httpStatus).body(error);
    }
}
