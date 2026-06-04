package com.uniremington.students.service;

import com.uniremington.students.dto.StudentRequestDTO;
import com.uniremington.students.dto.StudentResponseDTO;

import java.util.List;

/**
 * Contrato del servicio de administración de estudiantes.
 */
public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO dto);

    StudentResponseDTO getStudentById(Long id);

    List<StudentResponseDTO> getAllStudents();

    StudentResponseDTO updateStudent(Long id, StudentRequestDTO dto);

    void deleteStudent(Long id);

    StudentResponseDTO updateStudentStatus(Long id, Boolean active);
}
