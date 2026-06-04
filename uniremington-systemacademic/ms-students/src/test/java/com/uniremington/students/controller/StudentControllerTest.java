package com.uniremington.students.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import com.uniremington.students.exception.GlobalExceptionHandler;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.service.StudentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas unitarias del controlador de estudiantes con MockMvc standalone.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Controlador de estudiantes - pruebas unitarias")
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        StudentController controller = new StudentController(studentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/students: debe retornar 201 cuando se crea")
    void createStudent_returns201() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("Camila", "Rodriguez",
            "camila@uniremington.edu.co", true);
        StudentResponseDTO response = new StudentResponseDTO(1L, "Camila", "Rodriguez",
            "camila@uniremington.edu.co", true, LocalDateTime.now());

        when(studentService.createStudent(any())).thenReturn(response);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("camila@uniremington.edu.co"));
    }

    @Test
    @DisplayName("POST /api/students: debe retornar 400 con email inválido")
    void createStudent_invalidEmail_returns400() throws Exception {
        StudentRequestDTO invalid = new StudentRequestDTO("Camila", "Rodriguez",
            "no-es-email", true);

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/students/{id}: debe retornar 200")
    void getStudentById_returns200() throws Exception {
        StudentResponseDTO response = new StudentResponseDTO(1L, "Camila", "Rodriguez",
            "camila@uniremington.edu.co", true, LocalDateTime.now());

        when(studentService.getStudentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/students/{id}: debe retornar 404")
    void getStudentById_returns404() throws Exception {
        when(studentService.getStudentById(99L))
            .thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/students/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/students: debe retornar 200 con la lista")
    void getAllStudents_returns200() throws Exception {
        StudentResponseDTO response = new StudentResponseDTO(1L, "Camila", "Rodriguez",
            "camila@uniremington.edu.co", true, LocalDateTime.now());

        when(studentService.getAllStudents()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("PUT /api/students/{id}: debe retornar 200")
    void updateStudent_returns200() throws Exception {
        StudentRequestDTO request = new StudentRequestDTO("Camila", "Perez",
            "camila@uniremington.edu.co", true);
        StudentResponseDTO response = new StudentResponseDTO(1L, "Camila", "Perez",
            "camila@uniremington.edu.co", true, LocalDateTime.now());

        when(studentService.updateStudent(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lastName").value("Perez"));
    }

    @Test
    @DisplayName("PATCH /api/students/{id}/status: debe retornar 200")
    void updateStudentStatus_returns200() throws Exception {
        StudentResponseDTO response = new StudentResponseDTO(1L, "Camila", "Rodriguez",
            "camila@uniremington.edu.co", false, LocalDateTime.now());

        when(studentService.updateStudentStatus(1L, false)).thenReturn(response);

        mockMvc.perform(patch("/api/students/1/status").param("active", "false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("DELETE /api/students/{id}: debe retornar 204")
    void deleteStudent_returns204() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/students/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/students/{id}: debe retornar 404")
    void deleteStudent_returns404() throws Exception {
        doThrow(new StudentNotFoundException(99L))
            .when(studentService).deleteStudent(99L);

        mockMvc.perform(delete("/api/students/99"))
            .andExpect(status().isNotFound());
    }
}
