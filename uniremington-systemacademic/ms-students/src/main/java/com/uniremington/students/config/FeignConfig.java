package com.uniremington.students.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración compartida de los clientes Feign.
 *
 * <p>Define el nivel de logging por defecto. Otras opciones como
 * timeouts, retries o codificadores se configuran en {@code application.yml}.
 */
@Configuration
public class FeignConfig {

    /**
     * Nivel de logging de Feign: BASIC registra método, URL,
     * código de respuesta y tiempo de ejecución.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
