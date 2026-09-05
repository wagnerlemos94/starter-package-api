package br.com.digidatasistemas.starterPackage.exception;

public record FieldErrorResponse(
        String field,
        String message
) {}
