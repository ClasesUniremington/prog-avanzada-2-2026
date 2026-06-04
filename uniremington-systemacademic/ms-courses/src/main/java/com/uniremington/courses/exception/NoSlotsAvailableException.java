package com.uniremington.courses.exception;

/**
 * Excepción de negocio lanzada cuando se intenta reservar un cupo
 * en un curso que ya no tiene cupos disponibles.
 * Se traduce a HTTP 409 (Conflict).
 */
public class NoSlotsAvailableException extends RuntimeException {

    public NoSlotsAvailableException(String message) {
        super(message);
    }

    public NoSlotsAvailableException(Long courseId) {
        super("No hay cupos disponibles para el curso con id: " + courseId);
    }
}
