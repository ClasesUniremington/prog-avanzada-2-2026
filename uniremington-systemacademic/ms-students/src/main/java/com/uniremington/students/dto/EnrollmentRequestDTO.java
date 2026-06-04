package com.uniremington.students.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para crear una nueva matrícula.
 * Recibe el identificador del estudiante y el del curso.
 */
public class EnrollmentRequestDTO {

    @NotNull(message = "El identificador del estudiante es obligatorio")
    @Positive(message = "El identificador del estudiante debe ser positivo")
    private Long studentId;

    @NotNull(message = "El identificador del curso es obligatorio")
    @Positive(message = "El identificador del curso debe ser positivo")
    private Long courseId;

    public EnrollmentRequestDTO() {
    }

    public EnrollmentRequestDTO(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
