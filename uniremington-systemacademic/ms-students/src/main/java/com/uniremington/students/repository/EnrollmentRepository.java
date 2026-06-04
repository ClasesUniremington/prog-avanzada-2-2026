package com.uniremington.students.repository;

import com.uniremington.students.domain.Enrollment;
import com.uniremington.students.domain.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Enrollment}.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, EnrollmentStatus status);
}
