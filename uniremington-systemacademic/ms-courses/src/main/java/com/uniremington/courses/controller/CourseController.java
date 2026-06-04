package com.uniremington.courses.controller;

import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import com.uniremington.courses.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST para la administración de cursos y control de cupos.
 *
 * <p>El prefijo {@code /api/courses} es coherente con el enrutamiento
 * configurado en el API Gateway, que reenvía todas las peticiones
 * {@code /api/courses/**} hacia este microservicio.
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Operaciones de gestión de cursos y control de cupos")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // =====================================================
    // CRUD básico
    // =====================================================

    @Operation(summary = "Crea un nuevo curso")
    @PostMapping
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO request) {
        CourseResponseDTO created = courseService.saveCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Obtiene un curso por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Lista todos los cursos disponibles")
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Actualiza un curso existente")
    @PutMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequestDTO request) {
        return ResponseEntity.ok(courseService.updateCourse(id, request));
    }

    @Operation(summary = "Elimina un curso por su identificador")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // Operaciones de cupos (consumidas por ms-students)
    // =====================================================

    @Operation(summary = "Reserva un cupo en el curso indicado")
    @PostMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveSlot(@PathVariable Long id) {
        courseService.reserveSlot(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Libera un cupo en el curso indicado")
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseSlot(@PathVariable Long id) {
        courseService.releaseSlot(id);
        return ResponseEntity.ok().build();
    }
}
