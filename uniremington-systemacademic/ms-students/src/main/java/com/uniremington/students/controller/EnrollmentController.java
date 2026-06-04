package com.uniremington.students.controller;

import com.uniremington.students.dto.EnrollmentRequestDTO;
import com.uniremington.students.dto.EnrollmentResponseDTO;
import com.uniremington.students.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST para la gestión de matrículas.
 */
@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments", description = "Operaciones de matrícula y cancelación")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Inscribe a un estudiante en un curso")
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(
            @Valid @RequestBody EnrollmentRequestDTO request) {
        EnrollmentResponseDTO created = enrollmentService.enrollStudent(
            request.getStudentId(), request.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Cancela una matrícula existente")
    @DeleteMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> cancelEnrollment(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.cancelEnrollment(id));
    }

    @Operation(summary = "Obtiene una matrícula por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @Operation(summary = "Lista todas las matrículas")
    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }
}
