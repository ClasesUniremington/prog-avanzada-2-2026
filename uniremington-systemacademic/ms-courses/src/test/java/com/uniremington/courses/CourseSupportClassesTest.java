package com.uniremington.courses;

import com.uniremington.courses.config.DataInitializer;
import com.uniremington.courses.config.OpenApiConfig;
import com.uniremington.courses.domain.Course;
import com.uniremington.courses.dto.CourseRequestDTO;
import com.uniremington.courses.dto.CourseResponseDTO;
import com.uniremington.courses.exception.CourseNotFoundException;
import com.uniremington.courses.exception.ErrorResponse;
import com.uniremington.courses.exception.NoSlotsAvailableException;
import com.uniremington.courses.mapper.CourseMapper;
import com.uniremington.courses.repository.CourseRepository;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Clases de soporte de cursos")
class CourseSupportClassesTest {

    @Test
    @DisplayName("Entidad de curso: debe exponer constructores, accesores, igualdad y valores por defecto antes de persistir")
    void courseEntity_behavesAsExpected() throws Exception {
        Course course = new Course();
        course.setId(1L);
        course.setCode("JAVA-101");
        course.setName("Java");
        course.setDescription("Intro");
        course.setAvailableSlots(10);

        Method onCreate = Course.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(course);

        Course sameId = Course.builder().id(1L).build();
        Course otherId = Course.builder().id(2L).build();

        assertThat(course.getId()).isEqualTo(1L);
        assertThat(course.getCode()).isEqualTo("JAVA-101");
        assertThat(course.getName()).isEqualTo("Java");
        assertThat(course.getDescription()).isEqualTo("Intro");
        assertThat(course.getAvailableSlots()).isEqualTo(10);
        assertThat(course.getCreatedAt()).isNotNull();
        assertThat(course).isEqualTo(sameId).isNotEqualTo(otherId).isNotEqualTo("course");
        assertThat(course.hashCode()).isEqualTo(sameId.hashCode());
    }

    @Test
    @DisplayName("DTOs y respuesta de error: deben exponer accesores y constructores")
    void dtosAndErrorResponse_behaveAsExpected() {
        CourseRequestDTO request = new CourseRequestDTO();
        request.setCode("SPRING-201");
        request.setName("Spring");
        request.setDescription("Boot");
        request.setAvailableSlots(20);

        LocalDateTime createdAt = LocalDateTime.now();
        CourseResponseDTO response = new CourseResponseDTO();
        response.setId(2L);
        response.setCode(request.getCode());
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        response.setAvailableSlots(request.getAvailableSlots());
        response.setCreatedAt(createdAt);

        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponse error = new ErrorResponse(400, "Bad Request", "Invalid", "/api/courses", List.of("code"));
        error.setTimestamp(timestamp);
        error.setStatus(409);
        error.setError("Conflict");
        error.setMessage("No slots");
        error.setPath("/api/courses/1/reserve");
        error.setDetails(List.of("availableSlots"));

        ErrorResponse emptyError = new ErrorResponse();

        assertThat(response.getId()).isEqualTo(2L);
        assertThat(response.getCode()).isEqualTo("SPRING-201");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(error.getTimestamp()).isEqualTo(timestamp);
        assertThat(error.getStatus()).isEqualTo(409);
        assertThat(error.getError()).isEqualTo("Conflict");
        assertThat(error.getMessage()).isEqualTo("No slots");
        assertThat(error.getPath()).isEqualTo("/api/courses/1/reserve");
        assertThat(error.getDetails()).containsExactly("availableSlots");
        assertThat(emptyError.getDetails()).isNull();
    }

    @Test
    @DisplayName("Mapeador de cursos: debe mapear entidad, respuesta y valores nulos")
    void courseMapper_mapsValuesAndNulls() {
        CourseMapper mapper = new CourseMapper();
        CourseRequestDTO request = new CourseRequestDTO("JAVA-101", "Java", "Intro", 30);

        Course entity = mapper.toEntity(request);
        entity.setId(1L);
        entity.setCreatedAt(LocalDateTime.now());
        CourseResponseDTO response = mapper.toResponseDto(entity);

        CourseRequestDTO update = new CourseRequestDTO("JAVA-102", "Advanced Java", "Advanced", 12);
        mapper.updateEntityFromDto(update, entity);
        mapper.updateEntityFromDto(null, entity);
        mapper.updateEntityFromDto(update, null);

        assertThat(entity.getCode()).isEqualTo("JAVA-102");
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    @DisplayName("Clases de configuración: deben exponer metadatos OpenAPI y cargar cursos iniciales cuando no hay datos")
    void configurationClasses_behaveAsExpected() throws Exception {
        OpenAPI openAPI = new OpenApiConfig().coursesOpenApi();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("UniRemington - Courses API");

        CourseRepository repository = mock(CourseRepository.class);
        when(repository.count()).thenReturn(0L);

        CommandLineRunner runner = new DataInitializer().loadInitialCourses(repository);
        runner.run();

        verify(repository).saveAll(argThat(courses -> {
            List<Course> list = new ArrayList<>();
            courses.forEach(list::add);
            return list.size() == 3
                && "JAVA-101".equals(list.get(0).getCode())
                && list.stream().allMatch(course -> course.getAvailableSlots() > 0);
        }));
    }

    @Test
    @DisplayName("Inicializador de datos: debe omitir la carga cuando ya existen cursos")
    void dataInitializer_skipsWhenDataExists() throws Exception {
        CourseRepository repository = mock(CourseRepository.class);
        when(repository.count()).thenReturn(1L);

        new DataInitializer().loadInitialCourses(repository).run();

        verify(repository, never()).saveAll(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Excepciones: deben soportar mensajes personalizados")
    void exceptions_supportCustomMessages() {
        assertThat(new CourseNotFoundException("missing").getMessage()).isEqualTo("missing");
        assertThat(new NoSlotsAvailableException("full").getMessage()).isEqualTo("full");
    }
}
