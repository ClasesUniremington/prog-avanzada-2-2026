package com.uniremington.students.exception;

/**
 * Excepción lanzada cuando se intenta operar sobre una matrícula
 * que no existe. Se traduce a HTTP 404.
 */
public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException(Long enrollmentId) {
        super("No se encontró la matrícula con id: " + enrollmentId);
    }
}
