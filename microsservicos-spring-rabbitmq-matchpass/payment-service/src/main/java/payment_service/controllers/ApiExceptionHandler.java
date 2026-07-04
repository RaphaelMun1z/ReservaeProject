package payment_service.controllers;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(
            Map.of(
                "timestamp",
                Instant.now().toString(),
                "status",
                HttpStatus.BAD_REQUEST.value(),
                "error",
                "Bad Request",
                "message",
                exception.getMessage()
            )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElse("Dados inválidos.");

        return ResponseEntity.badRequest().body(
            Map.of(
                "timestamp",
                Instant.now().toString(),
                "status",
                HttpStatus.BAD_REQUEST.value(),
                "error",
                "Validation Error",
                "message",
                message
            )
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
            Map.of(
                "timestamp",
                Instant.now().toString(),
                "status",
                HttpStatus.BAD_GATEWAY.value(),
                "error",
                "Payment Provider Error",
                "message",
                exception.getMessage()
            )
        );
    }
}