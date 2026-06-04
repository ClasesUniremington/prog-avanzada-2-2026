package com.uniremington.students.exception;

import feign.FeignException;
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
 * Manejador centralizado de excepciones para el microservicio de
 * estudiantes y matrículas.
 *
 * <p>Traduce las excepciones de negocio, las de validación y las
 * propagadas desde {@code ms-courses} vía Feign a respuestas HTTP
 * con códigos coherentes (200, 201, 400, 404, 409).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(
            StudentNotFoundException ex, HttpServletRequest request) {
        log.warn("Estudiante no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentNotFound(
            EnrollmentNotFoundException ex, HttpServletRequest request) {
        log.warn("Matrícula no encontrada: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(InactiveStudentException.class)
    public ResponseEntity<ErrorResponse> handleInactive(
            InactiveStudentException ex, HttpServletRequest request) {
        log.warn("Estudiante inactivo: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(EnrollmentAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleEnrollmentAlreadyCancelled(
            EnrollmentAlreadyCancelledException ex, HttpServletRequest request) {
        log.warn("Matrícula ya cancelada: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Maneja excepciones provenientes de ms-courses vía Feign.
     * Mapea el código HTTP remoto al código local correspondiente.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeign(
            FeignException ex, HttpServletRequest request) {
        int remoteStatus = ex.status();
        log.warn("Error remoto desde ms-courses. Status={}, mensaje={}", remoteStatus, ex.getMessage());

        // Mapear el status remoto al status local
        HttpStatus localStatus = switch (remoteStatus) {
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            case 400 -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.SERVICE_UNAVAILABLE;
        };

        String message = "Error comunicándose con ms-courses: " + extractRemoteMessage(ex);
        return build(localStatus, message, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Error no controlado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocurrió un error inesperado en el servidor", request);
    }

    // =====================================================
    // Métodos auxiliares
    // =====================================================

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }

    private String extractRemoteMessage(FeignException ex) {
        // El cuerpo del error remoto puede ser un JSON; aquí simplificamos
        try {
            return ex.contentUTF8() == null || ex.contentUTF8().isBlank()
                ? ex.getMessage()
                : ex.contentUTF8();
        } catch (Exception ignored) {
            return ex.getMessage();
        }
    }
}
