package com.uniremington.students.exception;

/**
 * Excepción de negocio lanzada cuando se intenta matricular a un
 * estudiante inactivo. Se traduce a HTTP 409.
 */
public class InactiveStudentException extends RuntimeException {

    public InactiveStudentException(String message) {
        super(message);
    }

    public InactiveStudentException(Long studentId) {
        super("El estudiante con id " + studentId + " no está activo y no puede matricularse");
    }
}
