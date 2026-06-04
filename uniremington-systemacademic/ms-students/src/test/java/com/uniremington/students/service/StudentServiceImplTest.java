package com.uniremington.students.service;

import com.uniremington.students.domain.Student;
import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.mapper.StudentMapper;
import com.uniremington.students.repository.StudentRepository;
import com.uniremington.students.service.impl.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de estudiantes.
 * No levanta contexto de Spring ni base de datos H2.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de estudiantes - pruebas unitarias")
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student sampleStudent;
    private StudentRequestDTO sampleRequest;
    private StudentResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        sampleStudent = Student.builder()
            .id(1L)
            .firstName("Camila")
            .lastName("Rodriguez")
            .email("camila@uniremington.edu.co")
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

        sampleRequest = new StudentRequestDTO("Camila", "Rodriguez",
            "camila@uniremington.edu.co", true);

        sampleResponse = new StudentResponseDTO(1L, "Camila", "Rodriguez",
            "camila@uniremington.edu.co", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Crear estudiante: debe persistir y retornar el estudiante creado")
    void createStudent_happyPath() {
        when(studentMapper.toEntity(sampleRequest)).thenReturn(sampleStudent);
        when(studentRepository.save(sampleStudent)).thenReturn(sampleStudent);
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        StudentResponseDTO result = studentService.createStudent(sampleRequest);

        assertThat(result.getEmail()).isEqualTo("camila@uniremington.edu.co");
        verify(studentRepository).save(sampleStudent);
    }

    @Test
    @DisplayName("Consultar estudiante por id: debe retornar el estudiante cuando existe")
    void getStudentById_found() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        StudentResponseDTO result = studentService.getStudentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consultar estudiante por id: debe lanzar excepción cuando no existe")
    void getStudentById_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(99L))
            .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    @DisplayName("Listar estudiantes: debe retornar la lista completa")
    void getAllStudents_returnsList() {
        when(studentRepository.findAll()).thenReturn(List.of(sampleStudent));
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        List<StudentResponseDTO> result = studentService.getAllStudents();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Actualizar estudiante: debe actualizar el estudiante existente")
    void updateStudent_happyPath() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(sampleStudent)).thenReturn(sampleStudent);
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        StudentResponseDTO result = studentService.updateStudent(1L, sampleRequest);

        assertThat(result).isNotNull();
        verify(studentMapper).updateEntityFromDto(sampleRequest, sampleStudent);
    }

    @Test
    @DisplayName("Actualizar estudiante: debe lanzar excepción cuando el estudiante no existe")
    void updateStudent_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(99L, sampleRequest))
            .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar estudiante: debe eliminar el estudiante existente")
    void deleteStudent_happyPath() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        studentService.deleteStudent(1L);

        verify(studentRepository).delete(sampleStudent);
    }

    @Test
    @DisplayName("Eliminar estudiante: debe lanzar excepción cuando el estudiante no existe")
    void deleteStudent_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(99L))
            .isInstanceOf(StudentNotFoundException.class);

        verify(studentRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Actualizar estado de estudiante: debe cambiar el estado a inactivo")
    void updateStudentStatus_toInactive() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(sampleStudent)).thenReturn(sampleStudent);
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        studentService.updateStudentStatus(1L, false);

        assertThat(sampleStudent.getActive()).isFalse();
        verify(studentRepository).save(sampleStudent);
    }

    @Test
    @DisplayName("Actualizar estado de estudiante: debe asumir inactivo cuando se recibe nulo")
    void updateStudentStatus_nullDefaultsToInactive() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));
        when(studentRepository.save(sampleStudent)).thenReturn(sampleStudent);
        when(studentMapper.toResponseDto(sampleStudent)).thenReturn(sampleResponse);

        studentService.updateStudentStatus(1L, null);

        assertThat(sampleStudent.getActive()).isFalse();
    }

    @Test
    @DisplayName("Actualizar estado de estudiante: debe lanzar excepción cuando el estudiante no existe")
    void updateStudentStatus_notFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudentStatus(99L, true))
            .isInstanceOf(StudentNotFoundException.class);
    }
}
