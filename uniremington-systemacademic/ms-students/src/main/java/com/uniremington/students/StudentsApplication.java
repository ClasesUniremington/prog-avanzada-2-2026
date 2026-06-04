package com.uniremington.students;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Punto de entrada del microservicio de gestión de estudiantes
 * y matrículas.
 *
 * <p>Expone operaciones de administración de estudiantes y orquesta
 * el flujo de inscripción mediante llamadas síncronas a ms-courses
 * a través de OpenFeign.
 *
 * <p>Puerto de escucha local por defecto: {@code 18082}.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class StudentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentsApplication.class, args);
    }
}
