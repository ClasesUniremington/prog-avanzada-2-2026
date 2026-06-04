package com.uniremington.courses.repository;

import com.uniremington.courses.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Course}.
 * Provee las operaciones CRUD estándar y consultas auxiliares.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Busca un curso por su código único de identificación académica.
     *
     * @param code código del curso (ej. JAVA-101)
     * @return el curso si existe
     */
    Optional<Course> findByCode(String code);

    /**
     * Verifica si existe un curso con el código dado.
     */
    boolean existsByCode(String code);
}
