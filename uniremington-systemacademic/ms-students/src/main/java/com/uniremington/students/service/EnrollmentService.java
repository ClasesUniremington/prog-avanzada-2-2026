package com.uniremington.students.service;

import com.uniremington.students.dto.EnrollmentResponseDTO;

import java.util.List;

/**
 * Contrato del servicio de orquestación de matrículas.
 *
 * <p>Coordina las llamadas a {@code ms-courses} para reservar y
 * liberar cupos como parte del ciclo de vida de una matrícula.
 */
public interface EnrollmentService {

    /**
     * Inscribe a un estudiante activo en un curso. El flujo:
     * <ol>
     *   <li>Valida que el estudiante exista.</li>
     *   <li>Valida que esté activo.</li>
     *   <li>Reserva el cupo en ms-courses vía Feign.</li>
     *   <li>Si la reserva fue exitosa, persiste la matrícula.</li>
     *   <li>Si falla, propaga el error sin persistir.</li>
     * </ol>
     */
    EnrollmentResponseDTO enrollStudent(Long studentId, Long courseId);

    /**
     * Cancela una matrícula:
     * <ol>
     *   <li>Cambia el estado local a CANCELLED.</li>
     *   <li>Llama a ms-courses para liberar el cupo.</li>
     * </ol>
     */
    EnrollmentResponseDTO cancelEnrollment(Long enrollmentId);

    EnrollmentResponseDTO getEnrollmentById(Long id);

    List<EnrollmentResponseDTO> getAllEnrollments();
}
