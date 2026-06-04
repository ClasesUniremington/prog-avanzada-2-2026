package com.uniremington.students.dto;

import com.uniremington.students.domain.EnrollmentStatus;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa una matrícula hacia el cliente.
 */
public class EnrollmentResponseDTO {

    private Long id;
    private Long studentId;
    private Long courseId;
    private EnrollmentStatus status;
    private LocalDateTime enrollmentDate;

    public EnrollmentResponseDTO() {
    }

    public EnrollmentResponseDTO(Long id, Long studentId, Long courseId,
                                 EnrollmentStatus status, LocalDateTime enrollmentDate) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.enrollmentDate = enrollmentDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
}
