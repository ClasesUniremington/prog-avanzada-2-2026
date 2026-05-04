# Sistema de Gestión de Nómina - Taller de Pruebas (Java Puro)

Este proyecto es la base para el Taller de la **Unidad 1** del módulo de **Lenguaje de Programación Avanzado II**. Su objetivo es que los estudiantes practiquen la automatización de pruebas unitarias y de integración, así como la simulación de dependencias (Mocks).

## Tecnologías y Herramientas

- **Java 21**: Uso de características modernas como *Switch Expressions*.
- **Arquitectura**: MVC / Capas en **Java Puro** (Sin frameworks como Spring Boot por ahora).
- **JUnit 6**: Framework de pruebas de última generación.
- **Mockito**: Herramienta para simular dependencias (Mocks y Stubs).

## Arquitectura del Proyecto

El sistema está dividido en las siguientes capas tradicionales, unidas manualmente en la clase `Main`:

1. **Modelo (`model`)**: Contiene el POJO `Employee` que representa los datos.
2. **Repositorio (`repository`)**: Interfaz `EmployeeRepository` y su implementación en memoria `InMemoryEmployeeRepository` para simular una base de datos.
3. **Servicio (`service`)**: Contiene la lógica del negocio (cálculo de nómina) en la clase `PayrollService`.
4. **Controlador (`controller`)**: Clase `PayrollController` que orquesta la petición y maneja las respuestas.

## Instrucciones para el Estudiante

El código de producción (`src/main/java/...`) ya está completamente implementado. Su tarea como estudiante es **construir la suite de pruebas** en la carpeta `src/test/java/...`.

1. **Pruebas Unitarias con Mockito (`PayrollServiceTest.java`)**: 
   - Deben implementar los métodos de prueba vacíos.
   - Aislar el servicio simulando el `EmployeeRepository` usando `@Mock`.
   - Verificar que la lógica de cálculo (8% de descuento, pago por horas, etc.) funcione correctamente.
2. **Pruebas de Integración (`PayrollIntegrationTest.java`)**:
   - Probar el flujo completo: Controlador -> Servicio -> Repositorio en Memoria.
   - Crear un empleado y verificar que los datos fluyan correctamente por todas las capas.

### Comandos Útiles

Para ejecutar la aplicación principal en consola y verla funcionar:
```bash
mvn compile
mvn exec:java
```

Para ejecutar sus pruebas automatizadas (una vez las hayan implementado):
```bash
mvn test
```
