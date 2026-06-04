package com.uniremington.courses.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Manejador centralizado de excepciones del microservicio de cursos.
 *
 * <p>Convierte las excepciones de negocio y validación en respuestas
 * HTTP con códigos y mensajes adecuados, siguiendo el contrato del
 * PDF: 200 OK, 201 Created, 400 Bad Request, 404 Not Found,
 * 409 Conflict.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja {@link CourseNotFoundException} → 404 Not Found.
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(
            CourseNotFoundException ex, HttpServletRequest request) {
        log.warn("Curso no encontrado: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Maneja {@link NoSlotsAvailableException} → 409 Conflict.
     */
    @ExceptionHandler(NoSlotsAvailableException.class)
    public ResponseEntity<ErrorResponse> handleNoSlots(
            NoSlotsAvailableException ex, HttpServletRequest request) {
        log.warn("Reserva rechazada por falta de cupos: {}", ex.getMessage());
        ErrorResponse body = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Maneja errores de validación de DTOs → 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Construir lista de errores campo a campo
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(this::formatFieldError)
            .toList();

        ErrorResponse body = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            "Errores de validación en la petición",
            request.getRequestURI(),
            details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Maneja cualquier excepción no controlada → 500 Internal Server Error.
     * Previene la fuga de stacktraces hacia el cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Error no controlado", ex);
        ErrorResponse body = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "Ocurrió un error inesperado en el servidor",
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // =====================================================
    // Métodos auxiliares
    // =====================================================

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
