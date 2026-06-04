package com.uniremington.students.exception;

/**
 * Excepción de negocio lanzada cuando se intenta cancelar una matrícula
 * que ya se encuentra cancelada.
 */
public class EnrollmentAlreadyCancelledException extends RuntimeException {

    public EnrollmentAlreadyCancelledException(Long enrollmentId) {
        super("La matrícula con id " + enrollmentId + " ya se encuentra cancelada");
    }
}
