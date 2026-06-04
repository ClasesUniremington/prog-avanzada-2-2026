package com.uniremington.courses.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para las operaciones de creación y actualización
 * de cursos. Contiene los datos editables por el cliente, excluyendo
 * el {@code id} y la fecha de creación, que son gestionados por el
 * sistema.
 */
public class CourseRequestDTO {

    @NotBlank(message = "El código del curso es obligatorio")
    @Size(max = 50, message = "El código no puede superar los 50 caracteres")
    private String code;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "El número de cupos disponibles es obligatorio")
    @Min(value = 0, message = "Los cupos disponibles no pueden ser negativos")
    private Integer availableSlots;

    // =====================================================
    // Constructores
    // =====================================================

    public CourseRequestDTO() {
    }

    public CourseRequestDTO(String code, String name, String description, Integer availableSlots) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.availableSlots = availableSlots;
    }

    // =====================================================
    // Getters y Setters
    // =====================================================

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
}
