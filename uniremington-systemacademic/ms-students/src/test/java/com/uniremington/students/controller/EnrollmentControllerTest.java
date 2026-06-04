package com.uniremington.students.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.students.domain.EnrollmentStatus;
import com.uniremington.students.dto.EnrollmentRequestDTO;
import com.uniremington.students.dto.EnrollmentResponseDTO;
import com.uniremington.students.exception.EnrollmentAlreadyCancelledException;
import com.uniremington.students.exception.EnrollmentNotFoundException;
import com.uniremington.students.exception.GlobalExceptionHandler;
import com.uniremington.students.exception.InactiveStudentException;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del controlador de matrículas con MockMvc standalone.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Controlador de matrículas - pruebas unitarias")
class EnrollmentControllerTest {

    @Mock
    private EnrollmentService enrollmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        EnrollmentController controller = new EnrollmentController(enrollmentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/enrollments: debe retornar 201 cuando la matrícula es exitosa")
    void enroll_returns201() throws Exception {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO(1L, 100L);
        EnrollmentResponseDTO response = new EnrollmentResponseDTO(10L, 1L, 100L,
            EnrollmentStatus.ACTIVE, LocalDateTime.now());

        when(enrollmentService.enrollStudent(1L, 100L)).thenReturn(response);

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/enrollments: debe retornar 404 cuando el estudiante no existe")
    void enroll_studentNotFound_returns404() throws Exception {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO(99L, 100L);

        when(enrollmentService.enrollStudent(99L, 100L))
            .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/enrollments: debe retornar 409 cuando el estudiante está inactivo")
    void enroll_inactiveStudent_returns409() throws Exception {
        EnrollmentRequestDTO request = new EnrollmentRequestDTO(2L, 100L);

        when(enrollmentService.enrollStudent(2L, 100L))
            .thenThrow(new InactiveStudentException(2L));

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/enrollments: debe retornar 400 con datos inválidos")
    void enroll_invalidPayload_returns400() throws Exception {
        EnrollmentRequestDTO invalid = new EnrollmentRequestDTO(null, null);

        mockMvc.perform(post("/api/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/enrollments/{id}: debe retornar 200 al cancelar")
    void cancel_returns200() throws Exception {
        EnrollmentResponseDTO response = new EnrollmentResponseDTO(10L, 1L, 100L,
            EnrollmentStatus.CANCELLED, LocalDateTime.now());

        when(enrollmentService.cancelEnrollment(10L)).thenReturn(response);

        mockMvc.perform(delete("/api/enrollments/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("DELETE /api/enrollments/{id}: debe retornar 404 cuando no existe")
    void cancel_notFound_returns404() throws Exception {
        when(enrollmentService.cancelEnrollment(99L))
            .thenThrow(new EnrollmentNotFoundException(99L));

        mockMvc.perform(delete("/api/enrollments/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/enrollments/{id}: debe retornar 409 cuando ya está cancelada")
    void cancel_alreadyCancelled_returns409() throws Exception {
        when(enrollmentService.cancelEnrollment(10L))
            .thenThrow(new EnrollmentAlreadyCancelledException(10L));

        mockMvc.perform(delete("/api/enrollments/10"))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/enrollments/{id}: debe retornar 200")
    void getById_returns200() throws Exception {
        EnrollmentResponseDTO response = new EnrollmentResponseDTO(10L, 1L, 100L,
            EnrollmentStatus.ACTIVE, LocalDateTime.now());

        when(enrollmentService.getEnrollmentById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/enrollments/10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("GET /api/enrollments: debe retornar 200 con la lista")
    void getAll_returns200() throws Exception {
        EnrollmentResponseDTO response = new EnrollmentResponseDTO(10L, 1L, 100L,
            EnrollmentStatus.ACTIVE, LocalDateTime.now());

        when(enrollmentService.getAllEnrollments()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/enrollments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(10));
    }
}
