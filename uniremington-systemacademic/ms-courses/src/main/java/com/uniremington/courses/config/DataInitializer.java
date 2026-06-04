package com.uniremington.courses.config;

import com.uniremington.courses.domain.Course;
import com.uniremington.courses.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Carga datos iniciales de cursos al arrancar la aplicación.
 *
 * <p>Crea tres cursos de prueba siempre que la tabla esté vacía,
 * para facilitar las demostraciones y las pruebas manuales en
 * Postman y Swagger UI.
 *
 * <p>No se ejecuta en el perfil {@code test} para no contaminar las
 * pruebas unitarias.
 */
@Configuration
@Profile("!test")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner loadInitialCourses(CourseRepository courseRepository) {
        return args -> {
            // Sólo poblar si la tabla está vacía (evita duplicados en reinicios)
            if (courseRepository.count() > 0) {
                log.info("La tabla de cursos ya contiene datos. Omitiendo carga inicial.");
                return;
            }

            List<Course> initialCourses = List.of(
                Course.builder()
                    .code("JAVA-101")
                    .name("Introducción a Java")
                    .description("Curso introductorio al lenguaje Java y POO")
                    .availableSlots(30)
                    .createdAt(LocalDateTime.now())
                    .build(),
                Course.builder()
                    .code("SPRING-201")
                    .name("Spring Boot Fundamentals")
                    .description("Construcción de aplicaciones con Spring Boot")
                    .availableSlots(25)
                    .createdAt(LocalDateTime.now())
                    .build(),
                Course.builder()
                    .code("MICROSERVICES-301")
                    .name("Microservicios con Spring Cloud")
                    .description("Arquitectura de microservicios, Eureka, Gateway y OpenFeign")
                    .availableSlots(20)
                    .createdAt(LocalDateTime.now())
                    .build()
            );

            courseRepository.saveAll(initialCourses);
            log.info("Cargados {} cursos iniciales en la base de datos.", initialCourses.size());
        };
    }
}
