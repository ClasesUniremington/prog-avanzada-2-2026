package com.uniremington.students.service.impl;

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
import com.uniremington.students.service.EnrollmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de orquestación de matrículas.
 *
 * <p>Aplica el flujo definido en el PDF: valida estudiante, llama
 * remotamente a ms-courses para reservar/liberar cupos y persiste
 * el estado de la matrícula localmente.
 */
@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseClient courseClient;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseClient courseClient,
                                 EnrollmentMapper enrollmentMapper) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseClient = courseClient;
        this.enrollmentMapper = enrollmentMapper;
    }

    // =====================================================
    // Caso de uso 2: Enroll Student in Course
    // =====================================================

    @Override
    public EnrollmentResponseDTO enrollStudent(Long studentId, Long courseId) {
        log.debug("Iniciando matrícula. studentId={}, courseId={}", studentId, courseId);

        // 1. Buscar estudiante y validar existencia
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new StudentNotFoundException(studentId));

        // 2. Validar que esté activo
        if (Boolean.FALSE.equals(student.getActive())) {
            throw new InactiveStudentException(studentId);
        }

        // 3. Reservar cupo en ms-courses (Feign).
        //    Si falla, la excepción se propaga y la matrícula NO se persiste.
        courseClient.reserveSlot(courseId);
        log.info("Cupo reservado exitosamente para curso id={}", courseId);

        // 4. Persistir la matrícula localmente
        Enrollment enrollment = Enrollment.builder()
            .studentId(studentId)
            .courseId(courseId)
            .status(EnrollmentStatus.ACTIVE)
            .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Matrícula creada. id={}", saved.getId());

        return enrollmentMapper.toResponseDto(saved);
    }

    // =====================================================
    // Caso de uso 3: Cancel Enrollment
    // =====================================================

    @Override
    public EnrollmentResponseDTO cancelEnrollment(Long enrollmentId) {
        log.debug("Cancelando matrícula id={}", enrollmentId);

        // 1. Buscar matrícula
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new EnrollmentNotFoundException(enrollmentId));

        // 2. Evitar doble cancelación para no liberar el cupo más de una vez.
        if (EnrollmentStatus.CANCELLED.equals(enrollment.getStatus())) {
            throw new EnrollmentAlreadyCancelledException(enrollmentId);
        }

        // 3. Cambiar estado a CANCELLED (antes de la liberación remota,
        //    así si la llamada falla el estado local sigue consistente
        //    y la liberación se puede reintentar manualmente)
        enrollment.setStatus(EnrollmentStatus.CANCELLED);

        // 4. Llamar a ms-courses para liberar el cupo
        courseClient.releaseSlot(enrollment.getCourseId());
        log.info("Cupo liberado exitosamente para curso id={}", enrollment.getCourseId());

        // 5. Guardar cambios
        Enrollment updated = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toResponseDto(updated);
    }

    // =====================================================
    // Consultas
    // =====================================================

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponseDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
            .orElseThrow(() -> new EnrollmentNotFoundException(id));
        return enrollmentMapper.toResponseDto(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
            .map(enrollmentMapper::toResponseDto)
            .toList();
    }
}
