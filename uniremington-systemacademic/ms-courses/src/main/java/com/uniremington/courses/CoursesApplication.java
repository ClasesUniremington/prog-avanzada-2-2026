package com.uniremington.courses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Punto de entrada del microservicio de gestión de cursos.
 *
 * <p>Expone operaciones de administración del catálogo de cursos y
 * controla la disponibilidad de cupos mediante las operaciones de
 * reserva y liberación. Se registra dinámicamente en Eureka para que
 * el API Gateway y otros microservicios puedan descubrirlo.
 *
 * <p>Puerto de escucha local por defecto: {@code 18081}.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CoursesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoursesApplication.class, args);
    }
}
