package com.uniremington.students.config;

import com.uniremington.students.domain.Student;
import com.uniremington.students.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga datos iniciales de estudiantes al arrancar la aplicación.
 *
 * <p>Crea tres estudiantes de prueba (dos activos y uno inactivo)
 * para facilitar las demostraciones y validar los flujos de matrícula
 * con estudiante inactivo (debe rechazarse).
 */
@Configuration
@Profile("!test")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner loadInitialStudents(StudentRepository studentRepository) {
        return args -> {
            if (studentRepository.count() > 0) {
                log.info("La tabla de estudiantes ya contiene datos. Omitiendo carga inicial.");
                return;
            }

            List<Student> initialStudents = List.of(
                Student.builder()
                    .firstName("Camila")
                    .lastName("Rodriguez")
                    .email("camila.rodriguez@uniremington.edu.co")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build(),
                Student.builder()
                    .firstName("Andres")
                    .lastName("Gomez")
                    .email("andres.gomez@uniremington.edu.co")
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build(),
                Student.builder()
                    .firstName("Laura")
                    .lastName("Martinez")
                    .email("laura.martinez@uniremington.edu.co")
                    .active(false)
                    .createdAt(LocalDateTime.now())
                    .build()
            );

            studentRepository.saveAll(initialStudents);
            log.info("Cargados {} estudiantes iniciales en la base de datos.", initialStudents.size());
        };
    }
}
