package com.uniremington.students.domain;

/**
 * Estados posibles de una matrícula.
 *
 * <ul>
 *   <li>{@code ACTIVE}: matrícula vigente, con cupo reservado en el curso.</li>
 *   <li>{@code CANCELLED}: matrícula cancelada, con cupo liberado en el curso.</li>
 * </ul>
 */
public enum EnrollmentStatus {

    ACTIVE,
    CANCELLED
}
