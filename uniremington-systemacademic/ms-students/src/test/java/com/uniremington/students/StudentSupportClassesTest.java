package com.uniremington.students;

import com.uniremington.students.config.DataInitializer;
import com.uniremington.students.config.FeignConfig;
import com.uniremington.students.config.OpenApiConfig;
import com.uniremington.students.domain.Enrollment;
import com.uniremington.students.domain.EnrollmentStatus;
import com.uniremington.students.domain.Student;
import com.uniremington.students.dto.EnrollmentRequestDTO;
import com.uniremington.students.dto.EnrollmentResponseDTO;
import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import com.uniremington.students.exception.EnrollmentNotFoundException;
import com.uniremington.students.exception.ErrorResponse;
import com.uniremington.students.exception.InactiveStudentException;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.mapper.EnrollmentMapper;
import com.uniremington.students.mapper.StudentMapper;
import com.uniremington.students.repository.StudentRepository;
import feign.Logger;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Clases de soporte de estudiantes")
class StudentSupportClassesTest {

    @Test
    @DisplayName("Entidad de estudiante: debe exponer constructores, accesores, igualdad y valores por defecto antes de persistir")
    void studentEntity_behavesAsExpected() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Camila");
        student.setLastName("Rodriguez");
        student.setEmail("camila@uniremington.edu.co");

        Method onCreate = Student.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(student);

        Student sameId = Student.builder().id(1L).build();
        Student otherId = Student.builder().id(2L).build();

        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getFirstName()).isEqualTo("Camila");
        assertThat(student.getLastName()).isEqualTo("Rodriguez");
        assertThat(student.getEmail()).isEqualTo("camila@uniremington.edu.co");
        assertThat(student.getActive()).isTrue();
        assertThat(student.getCreatedAt()).isNotNull();
        assertThat(student).isEqualTo(sameId).isNotEqualTo(otherId).isNotEqualTo("student");
        assertThat(student.hashCode()).isEqualTo(sameId.hashCode());
    }

    @Test
    @DisplayName("Entidad de matrícula: debe exponer constructores, accesores, igualdad y valores por defecto antes de persistir")
    void enrollmentEntity_behavesAsExpected() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(10L);
        enrollment.setStudentId(1L);
        enrollment.setCourseId(100L);

        Method onCreate = Enrollment.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(enrollment);

        Enrollment sameId = Enrollment.builder().id(10L).build();
        Enrollment otherId = Enrollment.builder().id(11L).build();

        assertThat(enrollment.getId()).isEqualTo(10L);
        assertThat(enrollment.getStudentId()).isEqualTo(1L);
        assertThat(enrollment.getCourseId()).isEqualTo(100L);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getEnrollmentDate()).isNotNull();
        assertThat(enrollment).isEqualTo(sameId).isNotEqualTo(otherId).isNotEqualTo("enrollment");
        assertThat(enrollment.hashCode()).isEqualTo(sameId.hashCode());
        assertThat(EnrollmentStatus.valueOf("CANCELLED")).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("DTOs y respuesta de error: deben exponer accesores y constructores")
    void dtosAndErrorResponse_behaveAsExpected() {
        StudentRequestDTO studentRequest = new StudentRequestDTO();
        studentRequest.setFirstName("Andres");
        studentRequest.setLastName("Gomez");
        studentRequest.setEmail("andres@uniremington.edu.co");
        studentRequest.setActive(false);

        StudentResponseDTO studentResponse = new StudentResponseDTO();
        studentResponse.setId(1L);
        studentResponse.setFirstName(studentRequest.getFirstName());
        studentResponse.setLastName(studentRequest.getLastName());
        studentResponse.setEmail(studentRequest.getEmail());
        studentResponse.setActive(studentRequest.getActive());
        studentResponse.setCreatedAt(LocalDateTime.now());

        EnrollmentRequestDTO enrollmentRequest = new EnrollmentRequestDTO();
        enrollmentRequest.setStudentId(1L);
        enrollmentRequest.setCourseId(100L);

        EnrollmentResponseDTO enrollmentResponse = new EnrollmentResponseDTO();
        enrollmentResponse.setId(10L);
        enrollmentResponse.setStudentId(enrollmentRequest.getStudentId());
        enrollmentResponse.setCourseId(enrollmentRequest.getCourseId());
        enrollmentResponse.setStatus(EnrollmentStatus.ACTIVE);
        enrollmentResponse.setEnrollmentDate(LocalDateTime.now());

        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(404, "Not Found", "Missing", "/api/students", List.of("id"));
        error.setTimestamp(timestamp);
        error.setStatus(409);
        error.setError("Conflict");
        error.setMessage("Inactive");
        error.setPath("/api/enrollments");
        error.setDetails(List.of("student"));

        ErrorResponse emptyError = new ErrorResponse();

        assertThat(studentResponse.getLastName()).isEqualTo("Gomez");
        assertThat(studentResponse.getCreatedAt()).isNotNull();
        assertThat(enrollmentResponse.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollmentResponse.getEnrollmentDate()).isNotNull();
        assertThat(error.getTimestamp()).isEqualTo(timestamp);
        assertThat(error.getStatus()).isEqualTo(409);
        assertThat(error.getError()).isEqualTo("Conflict");
        assertThat(error.getMessage()).isEqualTo("Inactive");
        assertThat(error.getPath()).isEqualTo("/api/enrollments");
        assertThat(error.getDetails()).containsExactly("student");
        assertThat(emptyError.getDetails()).isNull();
    }

    @Test
    @DisplayName("Mapeadores: deben mapear entidades, respuestas y valores nulos")
    void mappers_mapValuesAndNulls() {
        StudentMapper studentMapper = new StudentMapper();
        StudentRequestDTO request = new StudentRequestDTO("Camila", "Rodriguez",
            "camila@uniremington.edu.co", null);

        Student student = studentMapper.toEntity(request);
        student.setId(1L);
        student.setCreatedAt(LocalDateTime.now());
        StudentResponseDTO response = studentMapper.toResponseDto(student);

        StudentRequestDTO update = new StudentRequestDTO("Laura", "Martinez",
            "laura@uniremington.edu.co", false);
        studentMapper.updateEntityFromDto(update, student);
        studentMapper.updateEntityFromDto(new StudentRequestDTO("Laura", "Martinez",
            "laura@uniremington.edu.co", null), student);
        studentMapper.updateEntityFromDto(null, student);
        studentMapper.updateEntityFromDto(update, null);

        Enrollment enrollment = Enrollment.builder()
            .id(10L)
            .studentId(1L)
            .courseId(100L)
            .status(EnrollmentStatus.ACTIVE)
            .enrollmentDate(LocalDateTime.now())
            .build();
        EnrollmentMapper enrollmentMapper = new EnrollmentMapper();
        EnrollmentResponseDTO enrollmentResponse = enrollmentMapper.toResponseDto(enrollment);

        assertThat(student.getFirstName()).isEqualTo("Laura");
        assertThat(student.getActive()).isFalse();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(studentMapper.toEntity(null)).isNull();
        assertThat(studentMapper.toResponseDto(null)).isNull();
        assertThat(enrollmentResponse.getId()).isEqualTo(10L);
        assertThat(enrollmentMapper.toResponseDto(null)).isNull();
    }

    @Test
    @DisplayName("Clases de configuración: deben exponer metadatos OpenAPI, logging de Feign y cargar estudiantes iniciales")
    void configurationClasses_behaveAsExpected() throws Exception {
        OpenAPI openAPI = new OpenApiConfig().studentsOpenApi();
        Logger.Level level = new FeignConfig().feignLoggerLevel();

        StudentRepository repository = mock(StudentRepository.class);
        when(repository.count()).thenReturn(0L);

        CommandLineRunner runner = new DataInitializer().loadInitialStudents(repository);
        runner.run();

        verify(repository).saveAll(argThat(students -> {
            List<Student> list = new ArrayList<>();
            students.forEach(list::add);
            return list.size() == 3
                && "Camila".equals(list.get(0).getFirstName())
                && list.stream().anyMatch(student -> Boolean.FALSE.equals(student.getActive()));
        }));
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("UniRemington - Students & Enrollments API");
        assertThat(level).isEqualTo(Logger.Level.BASIC);
    }

    @Test
    @DisplayName("Inicializador de datos: debe omitir la carga cuando ya existen estudiantes")
    void dataInitializer_skipsWhenDataExists() throws Exception {
        StudentRepository repository = mock(StudentRepository.class);
        when(repository.count()).thenReturn(1L);

        new DataInitializer().loadInitialStudents(repository).run();

        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Excepciones: deben soportar mensajes personalizados")
    void exceptions_supportCustomMessages() {
        assertThat(new StudentNotFoundException("missing student").getMessage()).isEqualTo("missing student");
        assertThat(new EnrollmentNotFoundException("missing enrollment").getMessage()).isEqualTo("missing enrollment");
        assertThat(new InactiveStudentException("inactive").getMessage()).isEqualTo("inactive");
    }
}
