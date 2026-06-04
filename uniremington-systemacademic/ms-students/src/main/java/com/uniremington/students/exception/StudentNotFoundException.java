package com.uniremington.students.exception;

/**
 * Excepción lanzada cuando se intenta operar sobre un estudiante
 * que no existe. Se traduce a HTTP 404.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(Long studentId) {
        super("No se encontró el estudiante con id: " + studentId);
    }
}
