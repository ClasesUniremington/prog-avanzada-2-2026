package com.uniremington.courses.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import com.uniremington.courses.exception.CourseNotFoundException;
import com.uniremington.courses.exception.GlobalExceptionHandler;
import com.uniremington.courses.exception.NoSlotsAvailableException;
import com.uniremington.courses.service.CourseService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del controlador de cursos usando MockMvc standalone.
 *
 * <p>No levanta el contexto completo de Spring: configura MockMvc
 * manualmente con el GlobalExceptionHandler para validar también los
 * mapeos de excepciones a códigos HTTP.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Controlador de cursos - pruebas unitarias con MockMvc standalone")
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        CourseController controller = new CourseController(courseService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    // =====================================================
    // POST /api/courses → 201 Created
    // =====================================================

    @Test
    @DisplayName("POST /api/courses: debe retornar 201 cuando el curso se crea correctamente")
    void createCourse_returns201() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("JAVA-101", "Java", "Intro", 30);
        CourseResponseDTO response = new CourseResponseDTO(
            1L, "JAVA-101", "Java", "Intro", 30, LocalDateTime.now());

        when(courseService.saveCourse(any())).thenReturn(response);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.code").value("JAVA-101"));
    }

    @Test
    @DisplayName("POST /api/courses: debe retornar 400 cuando faltan campos obligatorios")
    void createCourse_invalidPayload_returns400() throws Exception {
        // El campo 'code' está en blanco → debe fallar validación
        CourseRequestDTO invalid = new CourseRequestDTO("", "", null, -5);

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    // =====================================================
    // GET /api/courses/{id} → 200 / 404
    // =====================================================

    @Test
    @DisplayName("GET /api/courses/{id}: debe retornar 200 cuando el curso existe")
    void getCourseById_returns200() throws Exception {
        CourseResponseDTO response = new CourseResponseDTO(
            1L, "JAVA-101", "Java", "Intro", 30, LocalDateTime.now());

        when(courseService.getCourseById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/courses/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/courses/{id}: debe retornar 404 cuando el curso no existe")
    void getCourseById_returns404() throws Exception {
        when(courseService.getCourseById(99L))
            .thenThrow(new CourseNotFoundException(99L));

        mockMvc.perform(get("/api/courses/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    // =====================================================
    // GET /api/courses → 200
    // =====================================================

    @Test
    @DisplayName("GET /api/courses: debe retornar 200 con la lista")
    void getAllCourses_returns200() throws Exception {
        CourseResponseDTO response = new CourseResponseDTO(
            1L, "JAVA-101", "Java", "Intro", 30, LocalDateTime.now());

        when(courseService.getAllCourses()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("JAVA-101"));
    }

    // =====================================================
    // PUT /api/courses/{id} → 200 / 404
    // =====================================================

    @Test
    @DisplayName("PUT /api/courses/{id}: debe retornar 200 cuando se actualiza")
    void updateCourse_returns200() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("JAVA-101", "Java", "Intro", 25);
        CourseResponseDTO response = new CourseResponseDTO(
            1L, "JAVA-101", "Java", "Intro", 25, LocalDateTime.now());

        when(courseService.updateCourse(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableSlots").value(25));
    }

    @Test
    @DisplayName("PUT /api/courses/{id}: debe retornar 404 cuando el curso no existe")
    void updateCourse_returns404() throws Exception {
        CourseRequestDTO request = new CourseRequestDTO("JAVA-101", "Java", "Intro", 25);

        when(courseService.updateCourse(eq(99L), any()))
            .thenThrow(new CourseNotFoundException(99L));

        mockMvc.perform(put("/api/courses/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    // =====================================================
    // DELETE /api/courses/{id} → 204 / 404
    // =====================================================

    @Test
    @DisplayName("DELETE /api/courses/{id}: debe retornar 204 cuando se elimina")
    void deleteCourse_returns204() throws Exception {
        doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/courses/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/courses/{id}: debe retornar 404 cuando el curso no existe")
    void deleteCourse_returns404() throws Exception {
        doThrow(new CourseNotFoundException(99L))
            .when(courseService).deleteCourse(99L);

        mockMvc.perform(delete("/api/courses/99"))
            .andExpect(status().isNotFound());
    }

    // =====================================================
    // POST /api/courses/{id}/reserve → 200 / 404 / 409
    // =====================================================

    @Test
    @DisplayName("POST /api/courses/{id}/reserve: debe retornar 200 cuando hay cupos")
    void reserveSlot_returns200() throws Exception {
        doNothing().when(courseService).reserveSlot(1L);

        mockMvc.perform(post("/api/courses/1/reserve"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/courses/{id}/reserve: debe retornar 409 cuando no hay cupos")
    void reserveSlot_returns409() throws Exception {
        doThrow(new NoSlotsAvailableException(1L))
            .when(courseService).reserveSlot(1L);

        mockMvc.perform(post("/api/courses/1/reserve"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/courses/{id}/reserve: debe retornar 404 cuando el curso no existe")
    void reserveSlot_returns404() throws Exception {
        doThrow(new CourseNotFoundException(99L))
            .when(courseService).reserveSlot(99L);

        mockMvc.perform(post("/api/courses/99/reserve"))
            .andExpect(status().isNotFound());
    }

    // =====================================================
    // POST /api/courses/{id}/release → 200 / 404
    // =====================================================

    @Test
    @DisplayName("POST /api/courses/{id}/release: debe retornar 200")
    void releaseSlot_returns200() throws Exception {
        doNothing().when(courseService).releaseSlot(1L);

        mockMvc.perform(post("/api/courses/1/release"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/courses/{id}/release: debe retornar 404 cuando el curso no existe")
    void releaseSlot_returns404() throws Exception {
        doThrow(new CourseNotFoundException(99L))
            .when(courseService).releaseSlot(99L);

        mockMvc.perform(post("/api/courses/99/release"))
            .andExpect(status().isNotFound());
    }
}
