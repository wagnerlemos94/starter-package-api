package br.com.digidatasistemas.starterPackage.exception;

import br.com.digidata.crud.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        return response(HttpStatus.BAD_REQUEST, "Dados inválidos", request, errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErrorResponse> handleConflict(
            Exception ex,
            HttpServletRequest request
    ) {
        String message = ex instanceof ConflictException
                ? ex.getMessage()
                : "Não foi possível concluir a operação porque o recurso já existe ou está em uso.";

        return response(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        return response(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(
            Exception ex,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.FORBIDDEN,
                "Usuário não tem permissão para acessar esta funcionalidade.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        String errorId = UUID.randomUUID().toString();
        log.error("Erro interno não tratado. errorId={}", errorId, ex);

        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor",
                request,
                errorId,
                List.of()
        );
    }

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return response(status, message, request, List.of());
    }

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<FieldErrorResponse> errors
    ) {
        return response(status, message, request, UUID.randomUUID().toString(), errors);
    }

    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            String errorId,
            List<FieldErrorResponse> errors
    ) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.name(),
                message,
                request.getRequestURI(),
                errorId,
                errors
        );

        return ResponseEntity.status(status).body(body);
    }
}
