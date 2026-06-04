package com.uniremington.students.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa la matrícula de un estudiante en un curso.
 *
 * <p>El curso referenciado vive en el microservicio {@code ms-courses};
 * por ello solo se almacena el {@code courseId} como dato remoto.
 */
@Entity
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "enrollment_date", nullable = false, updatable = false)
    private LocalDateTime enrollmentDate;

    // =====================================================
    // Constructores
    // =====================================================

    public Enrollment() {
        // Constructor requerido por JPA
    }

    private Enrollment(Builder builder) {
        this.id = builder.id;
        this.studentId = builder.studentId;
        this.courseId = builder.courseId;
        this.status = builder.status;
        this.enrollmentDate = builder.enrollmentDate;
    }

    @PrePersist
    protected void onCreate() {
        if (this.enrollmentDate == null) {
            this.enrollmentDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EnrollmentStatus.ACTIVE;
        }
    }

    // =====================================================
    // Getters y Setters
    // =====================================================

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

    // =====================================================
    // equals / hashCode
    // =====================================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Enrollment other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // =====================================================
    // Builder estático
    // =====================================================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long studentId;
        private Long courseId;
        private EnrollmentStatus status;
        private LocalDateTime enrollmentDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder courseId(Long courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder status(EnrollmentStatus status) {
            this.status = status;
            return this;
        }

        public Builder enrollmentDate(LocalDateTime enrollmentDate) {
            this.enrollmentDate = enrollmentDate;
            return this;
        }

        public Enrollment build() {
            return new Enrollment(this);
        }
    }
}
