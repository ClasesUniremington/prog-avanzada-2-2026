package com.uniremington.courses.service;

import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;

import java.util.List;

/**
 * Contrato del servicio de gestión de cursos.
 *
 * <p>Define las operaciones de administración del catálogo de cursos
 * (CRUD) y las operaciones de control de cupos: reserva y liberación.
 */
public interface CourseService {

    /**
     * Crea un nuevo curso a partir de los datos del DTO.
     */
    CourseResponseDTO saveCourse(CourseRequestDTO dto);

    /**
     * Recupera un curso por su identificador.
     *
     * @throws com.uniremington.courses.exception.CourseNotFoundException
     *         si no existe.
     */
    CourseResponseDTO getCourseById(Long id);

    /**
     * Lista todos los cursos registrados.
     */
    List<CourseResponseDTO> getAllCourses();

    /**
     * Actualiza los datos editables de un curso existente.
     */
    CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto);

    /**
     * Elimina un curso por su identificador.
     */
    void deleteCourse(Long id);

    /**
     * Reserva un cupo en el curso, decrementando en uno los cupos
     * disponibles. Lanza {@code NoSlotsAvailableException} si los
     * cupos son cero.
     */
    void reserveSlot(Long courseId);

    /**
     * Libera un cupo en el curso, incrementando en uno los cupos
     * disponibles.
     */
    void releaseSlot(Long courseId);
}
