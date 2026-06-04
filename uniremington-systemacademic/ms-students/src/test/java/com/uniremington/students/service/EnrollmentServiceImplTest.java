package com.uniremington.students.service;

import com.uniremington.students.client.CourseClient;
import com.uniremington.students.domain.Enrollment;
import com.uniremington.students.domain.EnrollmentStatus;
import com.uniremington.students.domain.Student;
import com.uniremington.students.dto.EnrollmentResponseDTO;
import com.uniremington.students.exception.EnrollmentAlreadyCancelledException;
import com.uniremington.students.exception.EnrollmentNotFoundException;
import com.uniremington.students.exception.InactiveStudentException;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.mapper.EnrollmentMapper;
import com.uniremington.students.repository.EnrollmentRepository;
import com.uniremington.students.repository.StudentRepository;
import com.uniremington.students.service.impl.EnrollmentServiceImpl;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de matrículas.
 *
 * <p>Mockea el repositorio JPA, el repositorio de estudiantes y el
 * cliente Feign. NO levanta contexto de Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de matrículas - pruebas unitarias")
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseClient courseClient;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Student activeStudent;
    private Student inactiveStudent;
    private Enrollment sampleEnrollment;
    private EnrollmentResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        activeStudent = Student.builder()
            .id(1L)
            .firstName("Camila").lastName("Rodriguez")
            .email("camila@uniremington.edu.co")
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

        inactiveStudent = Student.builder()
            .id(2L)
            .firstName("Laura").lastName("Martinez")
            .email("laura@uniremington.edu.co")
            .active(false)
            .createdAt(LocalDateTime.now())
            .build();

        sampleEnrollment = Enrollment.builder()
            .id(10L)
            .studentId(1L)
            .courseId(100L)
            .status(EnrollmentStatus.ACTIVE)
            .enrollmentDate(LocalDateTime.now())
            .build();

        sampleResponse = new EnrollmentResponseDTO(10L, 1L, 100L,
            EnrollmentStatus.ACTIVE, LocalDateTime.now());
    }

    // =====================================================
    // enrollStudent: happy path
    // =====================================================

    @Test
    @DisplayName("Matricular estudiante: debe matricular cuando el estudiante está activo y hay cupo")
    void enrollStudent_happyPath() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        doNothing().when(courseClient).reserveSlot(100L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(sampleEnrollment);
        when(enrollmentMapper.toResponseDto(sampleEnrollment)).thenReturn(sampleResponse);

        EnrollmentResponseDTO result = enrollmentService.enrollStudent(1L, 100L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        verify(courseClient).reserveSlot(100L);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    // =====================================================
    // enrollStudent: validaciones
    // =====================================================

    @Test
    @DisplayName("Matricular estudiante: debe lanzar excepción si el estudiante no existe")
    void enrollStudent_studentNotFound() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(99L, 100L))
            .isInstanceOf(StudentNotFoundException.class);

        verify(courseClient, never()).reserveSlot(any());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Matricular estudiante: debe lanzar excepción si el estudiante está inactivo")
    void enrollStudent_inactive() {
        when(studentRepository.findById(2L)).thenReturn(Optional.of(inactiveStudent));

        assertThatThrownBy(() -> enrollmentService.enrollStudent(2L, 100L))
            .isInstanceOf(InactiveStudentException.class);

        verify(courseClient, never()).reserveSlot(any());
        verify(enrollmentRepository, never()).save(any());
    }

    // =====================================================
    // enrollStudent: fallo remoto Feign
    // =====================================================

    @Test
    @DisplayName("Matricular estudiante: no debe persistir si la reserva remota falla")
    void enrollStudent_feignFailure_doesNotSave() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        doThrow(buildFeignException(409)).when(courseClient).reserveSlot(100L);

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 100L))
            .isInstanceOf(FeignException.class);

        // La matrícula NUNCA debe persistirse si la reserva remota falla
        verify(enrollmentRepository, never()).save(any());
    }

    // =====================================================
    // cancelEnrollment
    // =====================================================

    @Test
    @DisplayName("Cancelar matrícula: debe cambiar el estado a cancelada y liberar el cupo")
    void cancelEnrollment_happyPath() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(sampleEnrollment));
        doNothing().when(courseClient).releaseSlot(100L);
        when(enrollmentRepository.save(sampleEnrollment)).thenReturn(sampleEnrollment);
        when(enrollmentMapper.toResponseDto(sampleEnrollment)).thenReturn(
            new EnrollmentResponseDTO(10L, 1L, 100L, EnrollmentStatus.CANCELLED, LocalDateTime.now()));

        EnrollmentResponseDTO result = enrollmentService.cancelEnrollment(10L);

        ArgumentCaptor<Enrollment> captor = ArgumentCaptor.forClass(Enrollment.class);
        verify(enrollmentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
        verify(courseClient).releaseSlot(100L);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("Cancelar matrícula: debe lanzar excepción si la matrícula no existe")
    void cancelEnrollment_notFound() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(99L))
            .isInstanceOf(EnrollmentNotFoundException.class);

        verify(courseClient, never()).releaseSlot(any());
    }

    @Test
    @DisplayName("Cancelar matrícula: debe propagar error remoto y no persistir si falla la liberación")
    void cancelEnrollment_releaseSlotFailure_doesNotSave() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(sampleEnrollment));
        doThrow(buildFeignException(503)).when(courseClient).releaseSlot(100L);

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(10L))
            .isInstanceOf(FeignException.class);

        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancelar matrícula: debe rechazar una matrícula ya cancelada sin liberar cupo")
    void cancelEnrollment_alreadyCancelled_doesNotReleaseSlot() {
        sampleEnrollment.setStatus(EnrollmentStatus.CANCELLED);
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(sampleEnrollment));

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(10L))
            .isInstanceOf(EnrollmentAlreadyCancelledException.class);

        verify(courseClient, never()).releaseSlot(any());
        verify(enrollmentRepository, never()).save(any());
    }

    // =====================================================
    // Consultas
    // =====================================================

    @Test
    @DisplayName("Consultar matrícula por id: debe retornar la matrícula cuando existe")
    void getEnrollmentById_found() {
        when(enrollmentRepository.findById(10L)).thenReturn(Optional.of(sampleEnrollment));
        when(enrollmentMapper.toResponseDto(sampleEnrollment)).thenReturn(sampleResponse);

        EnrollmentResponseDTO result = enrollmentService.getEnrollmentById(10L);

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Consultar matrícula por id: debe lanzar excepción cuando no existe")
    void getEnrollmentById_notFound() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.getEnrollmentById(99L))
            .isInstanceOf(EnrollmentNotFoundException.class);
    }

    @Test
    @DisplayName("Listar matrículas: debe retornar la lista completa")
    void getAllEnrollments_returnsList() {
        when(enrollmentRepository.findAll()).thenReturn(List.of(sampleEnrollment));
        when(enrollmentMapper.toResponseDto(sampleEnrollment)).thenReturn(sampleResponse);

        List<EnrollmentResponseDTO> result = enrollmentService.getAllEnrollments();

        assertThat(result).hasSize(1);
    }

    // =====================================================
    // Métodos auxiliares
    // =====================================================

    /**
     * Construye una FeignException con el código HTTP indicado para
     * simular respuestas de error desde ms-courses.
     */
    private FeignException buildFeignException(int status) {
        Request request = Request.create(
            Request.HttpMethod.POST,
            "http://ms-courses/api/courses/100/reserve",
            new HashMap<>(),
            Request.Body.empty(),
            new RequestTemplate()
        );
        return FeignException.errorStatus(
            "reserveSlot",
            feign.Response.builder()
                .status(status)
                .reason("Conflict")
                .request(request)
                .body("No hay cupos disponibles", StandardCharsets.UTF_8)
                .build()
        );
    }
}
