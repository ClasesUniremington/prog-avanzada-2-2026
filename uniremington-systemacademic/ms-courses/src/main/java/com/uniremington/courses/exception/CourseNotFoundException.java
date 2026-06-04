package com.uniremington.courses.exception;

/**
 * Excepción lanzada cuando se intenta operar sobre un curso que no
 * existe en la base de datos. Se traduce a HTTP 404 (Not Found).
 */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(Long courseId) {
        super("No se encontró el curso con id: " + courseId);
    }
}
