package com.uniremington.courses.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI para el microservicio
 * de cursos.
 *
 * <p>Personaliza los metadatos visibles en la página Swagger UI:
 * título, descripción, versión y datos de contacto. El resto de la
 * documentación se genera automáticamente a partir de las anotaciones
 * de los controllers.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI coursesOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("UniRemington - Courses API")
                .description("API para la administración de cursos y control de cupos")
                .version("1.0.0")
                .contact(new Contact()
                    .name("UniRemington")
                    .email("soporte@uniremington.edu.co"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
