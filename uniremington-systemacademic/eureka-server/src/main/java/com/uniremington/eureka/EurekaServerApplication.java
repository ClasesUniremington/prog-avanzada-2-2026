package com.uniremington.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Punto de entrada del Service Discovery de la plataforma UniRemington.
 *
 * <p>Esta aplicación arranca un servidor Netflix Eureka que actúa como
 * registro central en el que todos los microservicios de negocio se anuncian
 * dinámicamente. Una vez iniciado, el dashboard queda disponible en:
 * <pre>
 *     http://localhost:18761
 * </pre>
 *
 * <p>La anotación {@code @EnableEurekaServer} activa la auto-configuración
 * del servidor Eureka. No se requiere código de infraestructura adicional.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
