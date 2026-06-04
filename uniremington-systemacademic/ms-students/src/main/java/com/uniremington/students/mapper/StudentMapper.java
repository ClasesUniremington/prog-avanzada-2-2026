package com.uniremington.students.mapper;

import com.uniremington.students.domain.Student;
import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad {@link Student} y sus DTOs.
 */
@Component
public class StudentMapper {

    public Student toEntity(StudentRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Student.builder()
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .active(dto.getActive() == null ? Boolean.TRUE : dto.getActive())
            .build();
    }

    public StudentResponseDTO toResponseDto(Student entity) {
        if (entity == null) {
            return null;
        }
        return new StudentResponseDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getActive(),
            entity.getCreatedAt()
        );
    }

    public void updateEntityFromDto(StudentRequestDTO dto, Student entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }
    }
}
