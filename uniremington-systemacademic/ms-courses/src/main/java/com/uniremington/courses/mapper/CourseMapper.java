package com.uniremington.courses.mapper;

import com.uniremington.courses.domain.Course;
import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Realiza las conversiones entre la entidad {@link Course} y sus
 * DTOs de entrada y salida.
 *
 * <p>Se implementa como un {@code @Component} para mantenerlo
 * inyectable en services y facilitar las pruebas unitarias.
 */
@Component
public class CourseMapper {

    /**
     * Convierte un DTO de entrada en una nueva entidad {@link Course}.
     * No asigna {@code id} ni {@code createdAt} (se delegan a JPA).
     */
    public Course toEntity(CourseRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Course.builder()
            .code(dto.getCode())
            .name(dto.getName())
            .description(dto.getDescription())
            .availableSlots(dto.getAvailableSlots())
            .build();
    }

    /**
     * Convierte una entidad persistida en un DTO de respuesta.
     */
    public CourseResponseDTO toResponseDto(Course entity) {
        if (entity == null) {
            return null;
        }
        return new CourseResponseDTO(
            entity.getId(),
            entity.getCode(),
            entity.getName(),
            entity.getDescription(),
            entity.getAvailableSlots(),
            entity.getCreatedAt()
        );
    }

    /**
     * Actualiza los campos editables de una entidad existente con
     * los valores recibidos en el DTO. Conserva {@code id} y
     * {@code createdAt}.
     */
    public void updateEntityFromDto(CourseRequestDTO dto, Course entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAvailableSlots(dto.getAvailableSlots());
    }
}
