package com.uniremington.courses.service.impl;

import com.uniremington.courses.domain.Course;
import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import com.uniremington.courses.exception.CourseNotFoundException;
import com.uniremington.courses.exception.NoSlotsAvailableException;
import com.uniremington.courses.mapper.CourseMapper;
import com.uniremington.courses.repository.CourseRepository;
import com.uniremington.courses.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de cursos.
 *
 * <p>Centraliza la lógica de negocio del catálogo de cursos y
 * garantiza la integridad de las operaciones de reserva y liberación
 * de cupos. Todas las operaciones de escritura son transaccionales.
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public CourseServiceImpl(CourseRepository courseRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    // =====================================================
    // CRUD básico
    // =====================================================

    @Override
    public CourseResponseDTO saveCourse(CourseRequestDTO dto) {
        log.debug("Creando curso con código: {}", dto.getCode());
        Course entity = courseMapper.toEntity(dto);
        Course saved = courseRepository.save(entity);
        return courseMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = findCourseOrThrow(id);
        return courseMapper.toResponseDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(courseMapper::toResponseDto)
            .toList();
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        log.debug("Actualizando curso id: {}", id);
        Course existing = findCourseOrThrow(id);
        courseMapper.updateEntityFromDto(dto, existing);
        Course updated = courseRepository.save(existing);
        return courseMapper.toResponseDto(updated);
    }

    @Override
    public void deleteCourse(Long id) {
        log.debug("Eliminando curso id: {}", id);
        Course existing = findCourseOrThrow(id);
        courseRepository.delete(existing);
    }

    // =====================================================
    // Operaciones de control de cupos
    // =====================================================

    @Override
    public void reserveSlot(Long courseId) {
        log.debug("Reservando cupo en el curso id: {}", courseId);
        Course course = findCourseOrThrow(courseId);

        // Validar disponibilidad antes de decrementar
        if (course.getAvailableSlots() == null || course.getAvailableSlots() <= 0) {
            throw new NoSlotsAvailableException(courseId);
        }

        course.setAvailableSlots(course.getAvailableSlots() - 1);
        courseRepository.save(course);
        log.info("Cupo reservado. Curso id={}, cupos restantes={}", courseId, course.getAvailableSlots());
    }

    @Override
    public void releaseSlot(Long courseId) {
        log.debug("Liberando cupo en el curso id: {}", courseId);
        Course course = findCourseOrThrow(courseId);

        int current = course.getAvailableSlots() == null ? 0 : course.getAvailableSlots();
        course.setAvailableSlots(current + 1);
        courseRepository.save(course);
        log.info("Cupo liberado. Curso id={}, cupos actuales={}", courseId, course.getAvailableSlots());
    }

    // =====================================================
    // Métodos auxiliares
    // =====================================================

    /**
     * Recupera el curso o lanza {@link CourseNotFoundException} si no existe.
     */
    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new CourseNotFoundException(id));
    }
}
