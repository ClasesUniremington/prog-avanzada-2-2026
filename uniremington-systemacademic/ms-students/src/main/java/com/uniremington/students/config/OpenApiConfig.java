package com.uniremington.students.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI para el microservicio
 * de estudiantes y matrículas.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI studentsOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("UniRemington - Students & Enrollments API")
                .description("API para la administración de estudiantes y orquestación de matrículas")
                .version("1.0.0")
                .contact(new Contact()
                    .name("UniRemington")
                    .email("soporte@uniremington.edu.co"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
