package com.calculator.devops.dto;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        String path
) {
}