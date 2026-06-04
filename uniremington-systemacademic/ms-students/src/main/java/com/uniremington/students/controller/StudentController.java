package com.uniremington.students.controller;

import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import com.uniremington.students.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST para la administración de estudiantes.
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Operaciones de gestión de estudiantes")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Crea un nuevo estudiante")
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO request) {
        StudentResponseDTO created = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Obtiene un estudiante por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @Operation(summary = "Lista todos los estudiantes")
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @Operation(summary = "Actualiza un estudiante existente")
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDTO request) {
        return ResponseEntity.ok(studentService.updateStudent(id, request));
    }

    @Operation(summary = "Actualiza el estado activo/inactivo del estudiante")
    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentResponseDTO> updateStudentStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return ResponseEntity.ok(studentService.updateStudentStatus(id, active));
    }

    @Operation(summary = "Elimina un estudiante por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
