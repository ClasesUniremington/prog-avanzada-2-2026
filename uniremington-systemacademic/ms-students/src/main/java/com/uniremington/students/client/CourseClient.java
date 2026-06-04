package com.uniremington.students.client;

import com.uniremington.students.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Cliente Feign que consume las operaciones de control de cupos
 * del microservicio {@code ms-courses}.
 *
 * <p>El nombre lógico {@code ms-courses} se resuelve mediante Eureka.
 * Los endpoints invocados son:
 * <ul>
 *   <li>{@code POST /api/courses/{id}/reserve}</li>
 *   <li>{@code POST /api/courses/{id}/release}</li>
 * </ul>
 */
@FeignClient(
    name = "ms-courses",
    configuration = FeignConfig.class
)
public interface CourseClient {

    /**
     * Solicita la reserva de un cupo en el curso indicado.
     * Lanza FeignException con código 409 si no hay cupos disponibles.
     */
    @PostMapping("/api/courses/{id}/reserve")
    void reserveSlot(@PathVariable("id") Long courseId);

    /**
     * Solicita la liberación de un cupo en el curso indicado.
     */
    @PostMapping("/api/courses/{id}/release")
    void releaseSlot(@PathVariable("id") Long courseId);
}
