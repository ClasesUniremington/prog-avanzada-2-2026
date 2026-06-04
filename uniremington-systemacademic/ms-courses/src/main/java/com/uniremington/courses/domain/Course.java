package com.uniremington.courses.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un curso ofrecido por la universidad.
 *
 * <p>Cada curso tiene un código único de identificación académica,
 * un nombre, descripción y un contador de cupos disponibles que se
 * decrementa al matricular estudiantes y se incrementa al cancelar
 * matrículas.
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "available_slots", nullable = false)
    private Integer availableSlots;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // =====================================================
    // Constructores
    // =====================================================

    public Course() {
        // Constructor requerido por JPA
    }

    private Course(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.availableSlots = builder.availableSlots;
        this.createdAt = builder.createdAt;
    }

    // =====================================================
    // Callback JPA: asigna fecha de creación si no viene
    // =====================================================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(Integer availableSlots) {
        this.availableSlots = availableSlots;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // =====================================================
    // equals / hashCode basados en el id
    // =====================================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Course other)) return false;
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
        private String code;
        private String name;
        private String description;
        private Integer availableSlots;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder availableSlots(Integer availableSlots) {
            this.availableSlots = availableSlots;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Course build() {
            return new Course(this);
        }
    }
}
