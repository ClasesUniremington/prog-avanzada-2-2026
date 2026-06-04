package com.uniremington.students.dto;

import java.time.LocalDateTime;

/**
 * DTO de salida que representa un estudiante hacia el cliente.
 */
public class StudentResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean active;
    private LocalDateTime createdAt;

    public StudentResponseDTO() {
    }

    public StudentResponseDTO(Long id, String firstName, String lastName, String email,
                              Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.active = active;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
