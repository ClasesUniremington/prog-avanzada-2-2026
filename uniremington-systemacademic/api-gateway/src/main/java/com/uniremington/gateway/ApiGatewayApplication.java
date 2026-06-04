package com.uniremington.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Punto de entrada del API Gateway de UniRemington.
 *
 * <p>Esta aplicación arranca una instancia de Spring Cloud Gateway que actúa
 * como punto único de acceso perimetral de la plataforma. Todo el tráfico
 * externo dirigido a los microservicios de negocio debe pasar por este
 * gateway, el cual enruta las peticiones al servicio correspondiente usando
 * los nombres lógicos registrados en el servidor Eureka.
 *
 * <p>Puerto de escucha local por defecto: {@code 18080}.
 * <p>Prefijos de enrutamiento:
 * <ul>
 *   <li>{@code /api/courses/**}     → {@code ms-courses}</li>
 *   <li>{@code /api/students/**}    → {@code ms-students}</li>
 *   <li>{@code /api/enrollments/**} → {@code ms-students}</li>
 * </ul>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
