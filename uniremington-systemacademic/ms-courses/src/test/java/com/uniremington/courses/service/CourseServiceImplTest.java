package com.uniremington.courses.service;

import com.uniremington.courses.domain.Course;
import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import com.uniremington.courses.exception.CourseNotFoundException;
import com.uniremington.courses.exception.NoSlotsAvailableException;
import com.uniremington.courses.mapper.CourseMapper;
import com.uniremington.courses.repository.CourseRepository;
import com.uniremington.courses.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de cursos en aislamiento total.
 *
 * <p>Cumple con la regla del PDF: NO se levanta contexto de Spring,
 * NO se utiliza base de datos H2, todas las dependencias se simulan
 * con Mockito.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Servicio de cursos - pruebas unitarias")
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course sampleCourse;
    private CourseRequestDTO sampleRequest;
    private CourseResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        sampleCourse = Course.builder()
            .id(1L)
            .code("JAVA-101")
            .name("Introducción a Java")
            .description("Curso introductorio")
            .availableSlots(30)
            .createdAt(LocalDateTime.now())
            .build();

        sampleRequest = new CourseRequestDTO("JAVA-101", "Introducción a Java",
            "Curso introductorio", 30);

        sampleResponse = new CourseResponseDTO(1L, "JAVA-101", "Introducción a Java",
            "Curso introductorio", 30, LocalDateTime.now());
    }

    // =====================================================
    // saveCourse
    // =====================================================

    @Test
    @DisplayName("Crear curso: debe persistir y retornar el curso creado")
    void saveCourse_happyPath() {
        when(courseMapper.toEntity(sampleRequest)).thenReturn(sampleCourse);
        when(courseRepository.save(sampleCourse)).thenReturn(sampleCourse);
        when(courseMapper.toResponseDto(sampleCourse)).thenReturn(sampleResponse);

        CourseResponseDTO result = courseService.saveCourse(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo("JAVA-101");
        verify(courseRepository, times(1)).save(sampleCourse);
    }

    // =====================================================
    // getCourseById
    // =====================================================

    @Test
    @DisplayName("Consultar curso por id: debe retornar el curso cuando existe")
    void getCourseById_found() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));
        when(courseMapper.toResponseDto(sampleCourse)).thenReturn(sampleResponse);

        CourseResponseDTO result = courseService.getCourseById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Consultar curso por id: debe lanzar excepción cuando el curso no existe")
    void getCourseById_notFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
            .isInstanceOf(CourseNotFoundException.class)
            .hasMessageContaining("99");
    }

    // =====================================================
    // getAllCourses
    // =====================================================

    @Test
    @DisplayName("Listar cursos: debe retornar la lista completa")
    void getAllCourses_returnsList() {
        when(courseRepository.findAll()).thenReturn(List.of(sampleCourse));
        when(courseMapper.toResponseDto(sampleCourse)).thenReturn(sampleResponse);

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("JAVA-101");
    }

    @Test
    @DisplayName("Listar cursos: debe retornar lista vacía cuando no hay datos")
    void getAllCourses_emptyList() {
        when(courseRepository.findAll()).thenReturn(List.of());

        List<CourseResponseDTO> result = courseService.getAllCourses();

        assertThat(result).isEmpty();
    }

    // =====================================================
    // updateCourse
    // =====================================================

    @Test
    @DisplayName("Actualizar curso: debe actualizar el curso existente")
    void updateCourse_happyPath() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));
        when(courseRepository.save(sampleCourse)).thenReturn(sampleCourse);
        when(courseMapper.toResponseDto(sampleCourse)).thenReturn(sampleResponse);

        CourseResponseDTO result = courseService.updateCourse(1L, sampleRequest);

        assertThat(result).isNotNull();
        verify(courseMapper).updateEntityFromDto(sampleRequest, sampleCourse);
        verify(courseRepository).save(sampleCourse);
    }

    @Test
    @DisplayName("Actualizar curso: debe lanzar excepción cuando el curso no existe")
    void updateCourse_notFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.updateCourse(99L, sampleRequest))
            .isInstanceOf(CourseNotFoundException.class);

        verify(courseRepository, never()).save(any());
    }

    // =====================================================
    // deleteCourse
    // =====================================================

    @Test
    @DisplayName("Eliminar curso: debe eliminar el curso existente")
    void deleteCourse_happyPath() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        courseService.deleteCourse(1L);

        verify(courseRepository, times(1)).delete(sampleCourse);
    }

    @Test
    @DisplayName("Eliminar curso: debe lanzar excepción cuando el curso no existe")
    void deleteCourse_notFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.deleteCourse(99L))
            .isInstanceOf(CourseNotFoundException.class);

        verify(courseRepository, never()).delete(any());
    }

    // =====================================================
    // reserveSlot
    // =====================================================

    @Test
    @DisplayName("Reservar cupo: debe decrementar los cupos cuando hay disponibilidad")
    void reserveSlot_happyPath() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        courseService.reserveSlot(1L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        assertThat(captor.getValue().getAvailableSlots()).isEqualTo(29);
    }

    @Test
    @DisplayName("Reservar cupo: debe lanzar excepción cuando los cupos son cero")
    void reserveSlot_noSlots() {
        sampleCourse.setAvailableSlots(0);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        assertThatThrownBy(() -> courseService.reserveSlot(1L))
            .isInstanceOf(NoSlotsAvailableException.class)
            .hasMessageContaining("1");

        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reservar cupo: debe lanzar excepción cuando el curso no existe")
    void reserveSlot_courseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.reserveSlot(99L))
            .isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    @DisplayName("Reservar cupo: debe lanzar excepción cuando los cupos son nulos")
    void reserveSlot_nullSlots() {
        sampleCourse.setAvailableSlots(null);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        assertThatThrownBy(() -> courseService.reserveSlot(1L))
            .isInstanceOf(NoSlotsAvailableException.class);
    }

    // =====================================================
    // releaseSlot
    // =====================================================

    @Test
    @DisplayName("Liberar cupo: debe incrementar los cupos disponibles")
    void releaseSlot_happyPath() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        courseService.releaseSlot(1L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        assertThat(captor.getValue().getAvailableSlots()).isEqualTo(31);
    }

    @Test
    @DisplayName("Liberar cupo: debe inicializar en uno si los cupos son nulos")
    void releaseSlot_nullSlots() {
        sampleCourse.setAvailableSlots(null);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse));

        courseService.releaseSlot(1L);

        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(captor.capture());
        assertThat(captor.getValue().getAvailableSlots()).isEqualTo(1);
    }

    @Test
    @DisplayName("Liberar cupo: debe lanzar excepción cuando el curso no existe")
    void releaseSlot_courseNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.releaseSlot(99L))
            .isInstanceOf(CourseNotFoundException.class);
    }
}
