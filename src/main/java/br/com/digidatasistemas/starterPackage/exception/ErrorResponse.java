package br.com.digidatasistemas.starterPackage.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(

        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        String errorId,
        List<FieldErrorResponse> errors

) {}
