package com.uniremington.students.service.impl;

import com.uniremington.students.domain.Student;
import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;
import com.uniremington.students.exception.StudentNotFoundException;
import com.uniremington.students.mapper.StudentMapper;
import com.uniremington.students.repository.StudentRepository;
import com.uniremington.students.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de administración de estudiantes.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        log.debug("Creando estudiante con email: {}", dto.getEmail());
        Student entity = studentMapper.toEntity(dto);
        Student saved = studentRepository.save(entity);
        return studentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudentById(Long id) {
        Student student = findStudentOrThrow(id);
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
            .map(studentMapper::toResponseDto)
            .toList();
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto) {
        log.debug("Actualizando estudiante id: {}", id);
        Student existing = findStudentOrThrow(id);
        studentMapper.updateEntityFromDto(dto, existing);
        Student updated = studentRepository.save(existing);
        return studentMapper.toResponseDto(updated);
    }

    @Override
    public void deleteStudent(Long id) {
        log.debug("Eliminando estudiante id: {}", id);
        Student existing = findStudentOrThrow(id);
        studentRepository.delete(existing);
    }

    @Override
    public StudentResponseDTO updateStudentStatus(Long id, Boolean active) {
        log.debug("Actualizando estado del estudiante id={} a active={}", id, active);
        Student existing = findStudentOrThrow(id);
        existing.setActive(active == null ? Boolean.FALSE : active);
        Student updated = studentRepository.save(existing);
        return studentMapper.toResponseDto(updated);
    }

    // =====================================================
    // Métodos auxiliares
    // =====================================================

    private Student findStudentOrThrow(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new StudentNotFoundException(id));
    }
}
