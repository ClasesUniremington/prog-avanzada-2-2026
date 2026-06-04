package com.uniremington.students.mapper;

import com.uniremington.students.domain.Enrollment;
import com.uniremington.students.dto.EnrollmentResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad {@link Enrollment} y su DTO de salida.
 *
 * <p>No incluye {@code toEntity} porque las matrículas no se crean
 * a partir de un DTO simple: se construyen dentro del servicio luego
 * de validar el estudiante y reservar el cupo remoto.
 */
@Component
public class EnrollmentMapper {

    public EnrollmentResponseDTO toResponseDto(Enrollment entity) {
        if (entity == null) {
            return null;
        }
        return new EnrollmentResponseDTO(
            entity.getId(),
            entity.getStudentId(),
            entity.getCourseId(),
            entity.getStatus(),
            entity.getEnrollmentDate()
        );
    }
}
