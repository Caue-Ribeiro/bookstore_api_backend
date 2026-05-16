package com.caue.bookstore.controllers.exceptionHandler;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record CustomError(Instant timestamp, HttpStatus status, String error, String path) {}
