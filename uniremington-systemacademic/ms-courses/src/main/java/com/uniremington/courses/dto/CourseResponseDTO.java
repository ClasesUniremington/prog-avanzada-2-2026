package com.uniremington.courses.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida para representar un curso hacia el cliente.
 * Incluye todos los campos visibles del curso, incluyendo el
 * identificador y la fecha de creación.
 */
public class CourseResponseDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer availableSlots;
    private LocalDateTime createdAt;

    // =====================================================
    // Constructores
    // =====================================================

    public CourseResponseDTO() {
    }

    public CourseResponseDTO(Long id, String code, String name, String description,
                             Integer availableSlots, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.availableSlots = availableSlots;
        this.createdAt = createdAt;
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
}
