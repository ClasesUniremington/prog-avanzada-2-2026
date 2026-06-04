# 🎓 UniRemington System - Academic

### Sistema Académico Distribuido basado en Microservicios

Sistema académico distribuido basado en microservicios para la administración de estudiantes, cursos y matrículas. 
Desarrollado como proyecto final:

**Curso:** Lenguaje de Programación Avanzado II
**Docente:** Jose Castrillón
**Universidad:** UniRemington Manizales
**Fecha de entrega:** 4 de junio de 2026
**Versión del documento:** 1.0


![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.0-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue)
![JaCoCo](https://img.shields.io/badge/Coverage-%E2%89%A580%25-success)
![License](https://img.shields.io/badge/License-Apache%202.0-lightgrey)

---

## 📑 Contenido

- [Descripción](#-descripción)
- [Objetivos del Proyecto](#2-objetivos-del-proyecto)
- [Arquitectura](#-arquitectura)
- [Diagrama de Módulos](#-diagrama-de-módulos)
- [Stack Tecnológico](#-stack-tecnológico)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Puertos](#-puertos)
- [Cómo Ejecutar](#-cómo-ejecutar)
- [Cómo Probar](#-cómo-probar)
- [Documentación Swagger](#-documentación-swagger)
- [Dashboard Eureka](#-dashboard-eureka)
- [API Gateway](#-api-gateway)
- [Testing y Cobertura](#-testing-y-cobertura)
- [Colección Postman](#-colección-postman)
- [Decisiones Técnicas](#-decisiones-técnicas)
- [Autor](#-autor)

---

## 🎯 Descripción

**UniRemington** es un sistema académico distribuido compuesto por **4 microservicios independientes** que se comunican entre sí mediante REST. El sistema permite:

- ✅ Administración completa de estudiantes (CRUD + activación/desactivación).
- ✅ Administración completa de cursos (CRUD + control de cupos).
- ✅ Orquestación de matrículas con comunicación síncrona vía OpenFeign.
- ✅ Reserva y liberación automática de cupos al matricular/cancelar.
- ✅ Descubrimiento dinámico de servicios mediante Netflix Eureka.
- ✅ Acceso centralizado a través de Spring Cloud Gateway.
- ✅ Documentación OpenAPI/Swagger por microservicio.
- ✅ Pruebas unitarias con cobertura JaCoCo ≥80%.

¿Qué es UniRemington System - Academic?

**UniRemington System - Academic** es un sistema académico distribuido diseñado para gestionar el ciclo de vida completo de las inscripciones académicas en una universidad. El sistema permite administrar estudiantes, cursos y matrículas, garantizando la coherencia de los cupos disponibles mediante comunicación REST entre microservicios independientes.


¿Qué problema resuelve?

En una universidad real, la gestión de matrículas presenta varios desafíos técnicos:

- **Múltiples dominios independientes:** los estudiantes y los cursos son dominios separados que deben evolucionar de forma independiente.
- **Transacciones distribuidas:** matricular a un estudiante requiere coordinar la reserva de un cupo en otro sistema.
- **Consistencia de datos:** si la reserva del cupo falla, la matrícula no debe persistirse.
- **Escalabilidad:** el sistema debe poder crecer agregando más servicios sin acoplamientos rígidos.

UniRemington System - Academic resuelve estos problemas aplicando una **arquitectura de microservicios** con:

- **Service Discovery** (Eureka) para localizar servicios dinámicamente.
- **API Gateway** (Spring Cloud Gateway) como único punto de entrada.
- **Comunicación REST síncrona** (OpenFeign) entre servicios.
- **Bases de datos independientes** (H2 in-memory) por microservicio.
---
# Objetivos del Proyecto

## Objetivo General

Desarrollar un sistema académico distribuido compuesto por **cuatro microservicios independientes** que se comuniquen mediante REST, aplicando el patrón **MVC Clásico** y garantizando calidad mediante pruebas unitarias automatizadas con cobertura mínima del 80 %.

## Objetivos Específicos

1. **Implementar Service Discovery** con Netflix Eureka para registro y descubrimiento dinámico de servicios.
2. **Centralizar el acceso** mediante un API Gateway que enrute las peticiones por nombre lógico.
3. **Administrar el catálogo de cursos** con operaciones CRUD y control estricto de cupos disponibles.
4. **Administrar el ciclo de vida de estudiantes** con operaciones CRUD y activación/desactivación.
5. **Orquestar el flujo de matrículas** validando el estudiante, reservando el cupo remoto y persistiendo la matrícula localmente.
6. **Garantizar la consistencia** entre servicios mediante manejo de errores remotos con OpenFeign.
7. **Documentar las APIs** mediante OpenAPI/Swagger.
8. **Certificar la calidad** mediante pruebas unitarias con JUnit 6, Mockito 5.12 y reporte de cobertura JaCoCo.

## Casos de Uso Implementados

### MS-Courses (puerto 18081)

| # | Caso de Uso | Descripción |
|---|-------------|-------------|
| 1 | **Course Management** | Crear, leer, actualizar y eliminar cursos. |
| 2 | **Reserve Course Slots** | Disminuir en 1 los cupos disponibles. Lanza `NoSlotsAvailableException` si los cupos son 0. |
| 3 | **Release Course Slots** | Incrementar en 1 los cupos disponibles. |

### MS-Students (puerto 18082)

| # | Caso de Uso | Descripción |
|---|-------------|-------------|
| 1 | **Student Administration** | CRUD de estudiantes y cambio de estado (activo/inactivo). |
| 2 | **Enroll Student in Course** | Valida estudiante activo, reserva cupo vía Feign y persiste matrícula. |
| 3 | **Cancel Enrollment** | Cambia estado a `CANCELLED` y libera el cupo en `ms-courses`. |

---
## 🏗️ Arquitectura

El sistema sigue el patrón **MVC Clásico** (Controller → Service → Repository → Domain) en cada microservicio, con comunicación REST entre servicios:

```
                       ┌────────────────────────┐
                       │   CLIENTE EXTERNO      │
                       │  (Postman / Frontend)  │
                       └───────────┬────────────┘
                                   │ HTTP
                                   ▼
                       ┌────────────────────────┐
                       │   API GATEWAY :18080    │
                       │ (Spring Cloud Gateway) │
                       └───────────┬────────────┘
                                   │ Service Discovery
                                   ▼
                       ┌────────────────────────┐
                       │  EUREKA SERVER :18761   │
                       │   (Service Registry)   │
                       └────┬──────────────┬────┘
                            │              │
                ┌───────────┘              └──────────┐
                ▼                                     ▼
   ┌──────────────────────────┐         ┌──────────────────────────┐
   │   MS-STUDENTS :18082      │ Feign   │   MS-COURSES :18081       │
   │  - Student CRUD          │────────►│  - Course CRUD           │
   │  - Enrollment logic      │         │  - reserveSlot()         │
   │  - CourseClient (Feign)  │         │  - releaseSlot()         │
   │  - H2 DB independiente   │         │  - H2 DB independiente   │
   └──────────────────────────┘         └──────────────────────────┘
```

### Patrón MVC por microservicio

Cada microservicio de negocio (`ms-courses` y `ms-students`) sigue rigurosamente el patrón **Model-View-Controller** en su variante clásica adaptada a aplicaciones REST:

```
   HTTP Request
        │
        ▼
   ┌──────────────┐   Recibe peticiones HTTP
   │  Controller  │   Valida DTOs con @Valid
   └──────┬───────┘   Mapea HTTP ↔ DTO
          │
          ▼
   ┌──────────────┐   Lógica de negocio
   │   Service    │   Orquesta llamadas
   └──────┬───────┘   Lanza excepciones de negocio
          │
          ▼
   ┌──────────────┐   Acceso a datos (Spring Data JPA)
   │  Repository  │   Persistencia en H2
   └──────┬───────┘
          │
          ▼
   ┌──────────────┐   Entidades JPA
   │   Domain     │   Datos puros + anotaciones
   └──────────────┘

Componentes transversales:
   - Mapper:    Conversión Entity ↔ DTO
   - DTO:       Objetos de transferencia
   - Exception: Excepciones de negocio + GlobalExceptionHandler
   - Config:    Beans de configuración (OpenAPI, Feign, DataInitializer)
   - Client:    Clientes Feign (sólo en ms-students)

```

---

## 📦 Diagrama de Módulos

| Módulo | Responsabilidad | Puerto |
|--------|-----------------|--------|
| `eureka-server` | Registro y descubrimiento dinámico de servicios. Es el "directorio telefónico" del sistema.| `18761` |
| `api-gateway` | Único punto de entrada del exterior. Enruta peticiones a los microservicios usando nombres lógicos.| `18080` |
| `ms-courses` | Administra el catálogo de cursos y controla la disponibilidad de cupos| `18081` |
| `ms-students` | Ciclo de vida de estudiantes y orquestación de matrículas | `18082` |

---
## Comunicación Entre Servicios

La comunicación de `ms-students` hacia `ms-courses` se realiza mediante **OpenFeign + Spring Cloud LoadBalancer + Eureka**, en un flujo de tres pasos:

```
ms-students                Eureka                    ms-courses
    │                        │                           │
    │  1. ¿Dónde está        │                           │
    │     ms-courses?        │                           │
    ├───────────────────────►│                           │
    │                        │                           │
    │  2. Está en            │                           │
    │     192.168.x.x:18081   │                           │
    │◄───────────────────────┤                           │
    │                        │                           │
    │  3. POST /api/courses/1/reserve                    │
    ├────────────────────────────────────────────────────►│
    │                                                     │
    │  4. 200 OK (o 409 si no hay cupos)                  │
    │◄────────────────────────────────────────────────────┤
    │                                                     │
```
En este diseño **`ms-students` nunca conoce la URL física de `ms-courses`**: sólo conoce el nombre lógico `ms-courses` y deja que Eureka lo resuelva en tiempo de ejecución. Esto permite escalar horizontalmente sin reconfiguración.

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| Lenguaje | **Java 21** |
| Framework base | **Spring Boot 4.0.0** + **Spring Cloud 2025.1.0** |
| Microservicios | Netflix Eureka, Spring Cloud Gateway Server MVC, OpenFeign, Spring Cloud LoadBalancer |
| Persistencia | Spring Data JPA + H2 in-memory |
| Validación | Jakarta Bean Validation |
| Testing | JUnit 6 + Mockito 5.12 + MockMvc Standalone |
| Cobertura | JaCoCo ≥80% |
| Documentación | OpenAPI 3.0 / Swagger UI (springdoc 3.0.3) |
| Build | Maven 3.8+ multi-módulo |

> **Nota técnica:** Consultando la compatibilidad Spring Cloud 2024.0.0,está basada en Spring Boot 3.4.x y es incompatible con Spring Boot 4.0.0 (Spring Framework 7). Para preservar el requisito principal del PDF —**Spring Boot 4**— se actualizó Spring Cloud al release train oficialmente alineado: **2025.1.0 (Oakwood)**, publicado el 25 de noviembre de 2025.

---
## ¿Por qué Spring Boot + Spring Cloud?

Spring Boot proporciona:

- **Autoconfiguración inteligente:** detecta dependencias en el classpath y configura automáticamente componentes (Tomcat, JPA, etc.).
- **Starters:** dependencias agrupadas (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`).
- **Convención sobre configuración:** menos código boilerplate.
- **Ecosistema maduro:** una de las tecnologías más demandadas profesionalmente.

Spring Cloud añade:

- **Service Discovery** con Netflix Eureka.
- **API Gateway** con Spring Cloud Gateway (reactivo).
- **Cliente declarativo** con OpenFeign.
- **Balanceo de carga** con Spring Cloud LoadBalancer.

## ¿Por qué H2 in-memory?

H2 es una base de datos relacional escrita en Java que se puede ejecutar **en memoria**, sin requerir instalación externa. Ventajas:

- **Cero configuración:** funciona sin instalar nada extra.
- **Velocidad:** todas las operaciones son en RAM.
- **Compatible con SQL estándar:** se puede migrar a PostgreSQL/MySQL sin grandes cambios.
- **Consola web integrada:** se puede inspeccionar la BD desde el navegador (`/h2-console`).

La desventaja —los datos se pierden al reiniciar— es aceptable en un proyecto académico, y se compensa con un `DataInitializer` que repuebla la BD en cada arranque.

---
## 📁 Estructura del Proyecto

```
uniremington-systemacademic/
│
├── pom.xml                      ← Parent POM
├── README.md
├── .gitignore
├── postman/
│   └── UniRemington.postman_collection.json
│
├── eureka-server/               ← Service Discovery (18761)
│   ├── pom.xml
│   └── src/main/...
│
├── api-gateway/                 ← API Gateway (18080)
│   ├── pom.xml
│   └── src/main/...
│
├── ms-courses/                  ← Microservicio de cursos (18081)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/uniremington/courses/
│       │   ├── CoursesApplication.java
│       │   ├── config/          (OpenApiConfig, DataInitializer)
│       │   ├── controller/      (CourseController)
│       │   ├── service/         (interfaz + impl)
│       │   ├── repository/      (CourseRepository)
│       │   ├── domain/          (Course)
│       │   ├── dto/             (Request/Response)
│       │   ├── mapper/          (CourseMapper)
│       │   └── exception/       (Excepciones + GlobalHandler)
│       └── test/                (tests unitarios JUnit 6 + Mockito)
│
└── ms-students/                 ← Microservicio de estudiantes (18082)
    ├── pom.xml
    └── src/
        ├── main/java/com/uniremington/students/
        │   ├── StudentsApplication.java
        │   ├── config/          (OpenApi, Feign, DataInitializer)
        │   ├── controller/      (StudentController, EnrollmentController)
        │   ├── service/         (interfaces + impls)
        │   ├── repository/      (Student y Enrollment)
        │   ├── domain/          (Student, Enrollment, EnrollmentStatus)
        │   ├── dto/             (4 DTOs)
        │   ├── mapper/          (StudentMapper, EnrollmentMapper)
        │   ├── client/          (CourseClient - Feign)
        │   └── exception/       (Excepciones + GlobalHandler)
        └── test/                (tests con Mockito + MockMvc standalone)
```
## Interfaces de Servicio + Implementaciones

Cada servicio está dividido en una **interfaz** (`CourseService`) y una **implementación** (`CourseServiceImpl`).

### Ventajas

- Facilita el **mockeo en pruebas** (Mockito inyecta el mock contra la interfaz).
- Aplica el **Principio de Inversión de Dependencias** (SOLID-D).
- Permite múltiples implementaciones en el futuro sin romper código existente.
- Es la convención **profesional estándar** en Spring.

## Orden de Operaciones en `cancelEnrollment`

Cuando se cancela una matrícula, hay dos operaciones a realizar:

1. Cambiar el estado local a `CANCELLED`.
2. Llamar a `ms-courses` para liberar el cupo remoto.

### Decisión

**Primero** se actualiza el estado local, **luego** se hace la llamada remota.

### Justificación

Si la llamada remota falla:

- El estado local queda en `CANCELLED` (consistente con la intención del usuario).
- El cupo en `ms-courses` no se liberó, pero esto se puede **reintentar manualmente** o con un job programado.

Si se hubiera hecho al revés (primero remoto, luego local), un fallo entre ambas operaciones dejaría el cupo liberado pero la matrícula activa, lo cual permitiría **doble matriculación**.

## GlobalExceptionHandler Mapea `FeignException`

Cuando `ms-students` consume `ms-courses` y el remoto responde con error (p. ej. 409 sin cupos), el cliente Feign lanza una `FeignException`.

### Decisión

El `GlobalExceptionHandler` de `ms-students` captura `FeignException` y la traduce al código HTTP equivalente para el cliente final.

```java
HttpStatus localStatus = switch (remoteStatus) {
    case 404 -> HttpStatus.NOT_FOUND;
    case 409 -> HttpStatus.CONFLICT;
    case 400 -> HttpStatus.BAD_REQUEST;
    default -> HttpStatus.SERVICE_UNAVAILABLE;
};
```

### Ventaja

El cliente final (Postman/frontend) recibe códigos HTTP coherentes sin importar dónde se originó el error.

## MockMvc Standalone en lugar de @WebMvcTest

Para las pruebas de controlador se usa `MockMvcBuilders.standaloneSetup(...)` en lugar de `@WebMvcTest`.

### Ventajas

- **No carga el contexto de Spring**, por lo que los tests son sustancialmente más rápidos.
- Se alinea mejor con el espíritu "unitario" exigido para el proyecto final.
- Permite inyectar manualmente el `GlobalExceptionHandler` mediante `setControllerAdvice(...)`.

## JaCoCo Activado Sólo en Módulos de Negocio

El plugin de cobertura JaCoCo se activa **únicamente** en `ms-courses` y `ms-students`.

### Justificación

- `eureka-server` y `api-gateway` no contienen lógica de negocio testeable (son configuración pura).
- Activar JaCoCo en módulos sin tests provocaría que el build falle por cobertura del 0 %.

## Builder Pattern en Entidades JPA

Las entidades `Course`, `Student` y `Enrollment` exponen un **patrón Builder** estático interno.

### Ventajas

- **Legibilidad** en construcción de objetos con muchos campos.
- **Inmutabilidad parcial** durante la construcción.
- **Sin dependencias externas** (sin Lombok).

Ejemplo de uso:

```java
Course course = Course.builder()
    .code("JAVA-101")
    .name("Introducción a Java")
    .availableSlots(30)
    .build();
```
---
## ¿Por qué Maven Multi-Módulo?

Un proyecto multi-módulo permite:

- **Compilar todos los módulos con un solo comando** (`mvn clean install`).
- **Compartir configuración** desde el POM padre (versiones, plugins, propiedades).
- **Mantener cada módulo como una unidad desplegable independiente** (cada uno genera su propio JAR).
- **Reflejar la arquitectura de microservicios** en la estructura del repositorio.

## El Parent POM Explicado

El archivo `pom.xml` en la raíz cumple **cuatro funciones**:

### Función 1: Declarar el padre Spring Boot

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>4.0.0</version>
    <relativePath/>
</parent>
```

Esto hereda la gestión de versiones de más de 200 librerías y configura los plugins estándar.

### Función 2: Listar los módulos hijos

```xml
<modules>
    <module>eureka-server</module>
    <module>api-gateway</module>
    <module>ms-courses</module>
    <module>ms-students</module>
</modules>
```

### Función 3: Centralizar versiones de dependencias

```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2025.1.0</spring-cloud.version>
    <springdoc-openapi.version>3.0.3</springdoc-openapi.version>
    <jacoco.version>0.8.12</jacoco.version>
    <mockito.version>5.12.0</mockito.version>
    <jacoco.coverage.minimum>0.80</jacoco.coverage.minimum>
</properties>
```

### Función 4: Importar el BOM de Spring Cloud

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Esto permite que los módulos hijos usen `spring-cloud-starter-*` sin especificar versiones.

## Convenciones de Paquetes

Todos los módulos siguen la convención:

```
com.uniremington.<modulo>.<capa>
```

Donde `<modulo>` puede ser `eureka`, `gateway`, `courses`, `students`, y `<capa>` puede ser `controller`, `service`, `repository`, `domain`, `dto`, `mapper`, `exception`, `config`, `client`.

Esta convención facilita la navegación y la activación correcta de `@ComponentScan` automático de Spring Boot.

## Módulo 1: Eureka Server

## Propósito

El **Eureka Server** es el corazón del Service Discovery. Su única responsabilidad es mantener un registro actualizado de los servicios disponibles, sus direcciones físicas y su estado de salud.

Cuando un microservicio arranca, se "registra" en Eureka enviando un mensaje con:

- Su **nombre lógico** (ej. `ms-courses`).
- Su **dirección IP y puerto** (ej. `192.168.1.10:8081`).
- Su **estado** (`UP`, `DOWN`).

Cuando otro microservicio quiere consumir a `ms-courses`, le pregunta a Eureka: *"¿Dónde está `ms-courses` ahora mismo?"* y Eureka responde con la dirección física actual.

# Módulo 2: API Gateway

## Propósito

El **API Gateway** es el **único punto de entrada** al sistema desde el exterior. Centraliza el tráfico HTTP entrante y lo enruta al microservicio interno correspondiente.

### ¿Por qué un Gateway?

Sin un Gateway, los clientes externos tendrían que conocer las direcciones físicas de cada microservicio (8081, 8082...) y manejar el cambio cuando éstas varíen. Con un Gateway:

- **Una sola URL pública** (http://localhost:8080).
- **Enrutamiento transparente** por nombre lógico vía Eureka.
- **Posibilidad futura** de añadir filtros transversales (autenticación, logs, rate limiting, etc.).

## Spring Cloud Gateway: Reactivo

Una característica importante: **Spring Cloud Gateway está construido sobre WebFlux** (programación reactiva, no bloqueante), no sobre Spring MVC. Esto significa que:

- **No se puede incluir** `spring-boot-starter-web` (rompe el arranque).
- **El servidor embebido es Netty**, no Tomcat.
- El módulo es ligero y eficiente con muchas conexiones concurrentes.

## Anatomía de una ruta

Cada entrada en `routes` tiene tres elementos clave:

| Elemento | Significado |
|----------|-------------|
| `id` | Identificador único de la ruta (aparece en logs). |
| `uri: lb://ms-courses` | Destino. El esquema `lb://` indica al Gateway que use el LoadBalancer para resolver `ms-courses` consultando Eureka. |
| `predicates: - Path=/api/courses/**` | Condición de coincidencia. Si la URL entrante coincide con el patrón, se enruta. |

### Mapeo de rutas

| Prefijo externo | Microservicio interno |
|-----------------|----------------------|
| `/api/courses/**` | `ms-courses` |
| `/api/students/**` | `ms-students` |
| `/api/enrollments/**` | `ms-students` |

> **Importante:** El Gateway **conserva el path completo** al reenviar. Una petición a `http://localhost:8080/api/courses/1` llega a `ms-courses` como `/api/courses/1`. Por eso los controllers de los microservicios usan `@RequestMapping("/api/courses")`.

# Módulo 3: MS-Courses

## Propósito

`ms-courses` administra el **catálogo de cursos** y controla el **número de cupos disponibles** en cada uno. Es consumido por `ms-students` (vía Feign) cuando se necesita reservar o liberar un cupo durante el ciclo de vida de una matrícula.

## Módulo 4: MS-Students

## Propósito

`ms-students` es el **microservicio más complejo del sistema**. Tiene dos responsabilidades:

1. **Administrar estudiantes**: CRUD básico + cambio de estado activo/inactivo.
2. **Orquestar matrículas**: validar estudiante, reservar cupo remoto, persistir matrícula.

La orquestación de matrículas es el **corazón funcional** del proyecto y donde brilla la arquitectura de microservicios.


## 🚪 Puertos

| Servicio | Puerto | URL |
|----------|--------|-----|
| Eureka Server | `18761` | http://localhost:18761 |
| API Gateway | `18080` | http://localhost:18080 |
| MS Courses | `18081` | http://localhost:18081 |
| MS Students | `18082` | http://localhost:18082 |

Estos son los puertos locales por defecto configurados para evitar conflictos con herramientas comunes del equipo. Los puertos oficiales del PDF son `8761`, `8080`, `8081` y `8082`; si necesitas presentar la ejecución exactamente con esos valores, puedes sobrescribirlos por variables de entorno sin modificar el código.

| Servicio | Variable de entorno | Valor por defecto |
|----------|---------------------|-------------------|
| Eureka Server | `EUREKA_SERVER_PORT` | `18761` |
| API Gateway | `API_GATEWAY_PORT` | `18080` |
| MS Courses | `MS_COURSES_PORT` | `18081` |
| MS Students | `MS_STUDENTS_PORT` | `18082` |
| URL de Eureka usada por clientes | `EUREKA_SERVER_URL` | `http://localhost:18761/eureka/` |

---

## 🚀 Cómo Ejecutar

### Requisitos previos

- ☑️ **Java 21+** instalado (`java -version`)
- ☑️ **Maven 3.8+** instalado (`mvn -version`)
- ☑️ Puertos `18080`, `18081`, `18082` y `18761` disponibles, o definir puertos alternos por variables de entorno

### Paso 1: Importar en IntelliJ IDEA

1. Abrir IntelliJ IDEA.
2. **File → Open** → seleccionar la carpeta raíz `uniremington-systemacademic`.
3. IntelliJ detectará automáticamente el `pom.xml` padre y los 4 módulos.
4. Esperar a que se descarguen las dependencias.

### Paso 2: Compilar todo

```bash
mvn clean install
```

### Paso 3: Arrancar los servicios EN ORDEN

> ⚠️ **El orden es importante:** Eureka primero, luego los microservicios de negocio, y finalmente el Gateway.

**Terminal 1 — Eureka Server**
```bash
mvn spring-boot:run -pl eureka-server
```
Esperar a ver: `Started EurekaServerApplication in X seconds`

**Terminal 2 — MS Courses** (esperar ~10 segundos después de Eureka)
```bash
mvn spring-boot:run -pl ms-courses
```

**Terminal 3 — MS Students**
```bash
mvn spring-boot:run -pl ms-students
```

**Terminal 4 — API Gateway**
```bash
mvn spring-boot:run -pl api-gateway
```

### Ejecución con los puertos oficiales

**Terminal 1 — Eureka Server**
```powershell
$env:EUREKA_SERVER_PORT="18761"
mvn spring-boot:run -pl eureka-server
```

**Terminal 2 — MS Courses**
```powershell
$env:MS_COURSES_PORT="18081"
$env:EUREKA_SERVER_URL="http://localhost:18761/eureka/"
mvn spring-boot:run -pl ms-courses
```

**Terminal 3 — MS Students**
```powershell
$env:MS_STUDENTS_PORT="18082"
$env:EUREKA_SERVER_URL="http://localhost:18761/eureka/"
mvn spring-boot:run -pl ms-students
```

**Terminal 4 — API Gateway**
```powershell
$env:API_GATEWAY_PORT="18080"
$env:EUREKA_SERVER_URL="http://localhost:18761/eureka/"
mvn spring-boot:run -pl api-gateway
```

Con este modo, las peticiones externas se hacen por:

```text
http://localhost:18080/api/courses
http://localhost:18080/api/students
http://localhost:18080/api/enrollments
```

### Paso 4: Verificar el sistema

Abrir el dashboard de Eureka en http://localhost:18761 y comprobar que los 3 servicios (`api-gateway`, `ms-courses`, `ms-students`) aparezcan registrados con estado `UP`. Si se ejecuta con los puertos oficiales del PDF, abrir http://localhost:8761.

---

## 🧪 Cómo Probar

### Opción A: Postman

Importa la colección desde `postman/UniRemington.postman_collection.json` y ejecuta las peticiones agrupadas por carpeta:

- 🎓 **Courses** — CRUD + reserva/liberación de cupos
- 👥 **Students** — CRUD + cambio de estado
- 📚 **Enrollments** — Matrícula y cancelación (flujo orquestado)
- 🔍 **Infraestructura** — Eureka, health del Gateway, Swagger



### Códigos HTTP esperados

| Operación | Código |
|-----------|--------|
| Crear recurso | `201 Created` |
| Consulta exitosa | `200 OK` |
| Eliminación | `204 No Content` |
| Recurso no encontrado | `404 Not Found` |
| Validación fallida | `400 Bad Request` |
| Conflicto de negocio (sin cupos, estudiante inactivo) | `409 Conflict` |

---

## 📚 Documentación Swagger

Cada microservicio expone su propia documentación interactiva:

| Microservicio | Swagger UI |
|---------------|------------|
| MS Courses | http://localhost:18081/swagger-ui.html |
| MS Students | http://localhost:18082/swagger-ui.html |

Los esquemas OpenAPI JSON están disponibles en:

- http://localhost:18081/api-docs
- http://localhost:18082/api-docs

---

## 🔭 Dashboard Eureka

http://localhost:18761

Aquí podrás ver en tiempo real los servicios registrados, su estado (`UP`/`DOWN`) y la información de cada instancia.

---

## 🌐 API Gateway

Todas las peticiones externas deben dirigirse al **API Gateway** (puerto **18080**), nunca directamente a los puertos `18081` u `18082`.

### Rutas configuradas

| Prefijo | Destino |
|---------|---------|
| `/api/courses/**` | `ms-courses` |
| `/api/students/**` | `ms-students` |
| `/api/enrollments/**` | `ms-students` |

### Validación operativa del Gateway

```
GET http://localhost:18080/actuator/health
GET http://localhost:18080/api/courses
GET http://localhost:18080/api/students
```

---

## ✅ Testing y Cobertura

### Filosofía de testing

- **Nivel 1 (Services):** JUnit 6 + Mockito en aislamiento total. **Prohibido** `@SpringBootTest`, **prohibido** H2.
- **Nivel 2 (Controllers):** MockMvc Standalone con `@MockBean`/`@Mock`, sin levantar contexto Spring.

### Ejecutar pruebas

```bash
# Tests + verificación de cobertura ≥80%
mvn clean verify

# Sólo un microservicio
mvn clean verify -pl ms-courses
mvn clean verify -pl ms-students
```

### Reportes de cobertura JaCoCo

Tras ejecutar `mvn verify`, abrir en el navegador:

- `ms-courses/target/site/jacoco/index.html`
- `ms-students/target/site/jacoco/index.html`

> 🚨 **El build falla automáticamente** si la cobertura es inferior al 80% en cualquiera de los dos microservicios.

---

## Casos Cubiertos por las Pruebas

### CourseServiceImplTest (14 tests)

- ✅ `saveCourse_happyPath`
- ✅ `getCourseById_found`
- ✅ `getCourseById_notFound`
- ✅ `getAllCourses_returnsList`
- ✅ `getAllCourses_emptyList`
- ✅ `updateCourse_happyPath`
- ✅ `updateCourse_notFound`
- ✅ `deleteCourse_happyPath`
- ✅ `deleteCourse_notFound`
- ✅ `reserveSlot_happyPath`
- ✅ `reserveSlot_noSlots` (lanza NoSlotsAvailableException)
- ✅ `reserveSlot_courseNotFound`
- ✅ `reserveSlot_nullSlots`
- ✅ `releaseSlot_happyPath`
- ✅ `releaseSlot_nullSlots`
- ✅ `releaseSlot_courseNotFound`

### EnrollmentServiceImplTest (10 tests)

- ✅ `enrollStudent_happyPath`
- ✅ `enrollStudent_studentNotFound`
- ✅ `enrollStudent_inactive`
- ✅ `enrollStudent_feignFailure_doesNotSave` (CRÍTICO)
- ✅ `cancelEnrollment_happyPath`
- ✅ `cancelEnrollment_notFound`
- ✅ `getEnrollmentById_found`
- ✅ `getEnrollmentById_notFound`
- ✅ `getAllEnrollments_returnsList`

## 📮 Colección Postman

Disponible en: `postman/UniRemington.postman_collection.json`

**Importación:**
1. Abrir Postman → **File → Import**.
2. Seleccionar el archivo de colección.
3. Listo. La variable `{{gateway}}` ya apunta a `http://localhost:18080`.

---
# Flujos de Negocio Detallados

## Flujo Completo de Matrícula

### Paso a paso

1. **Cliente externo (Postman)** envía:
   ```http
   POST http://localhost:8080/api/enrollments
   {"studentId": 1, "courseId": 1}
   ```

2. **API Gateway** recibe la petición, identifica el path `/api/enrollments/**` y lo enruta a `ms-students` (resuelto vía Eureka).

3. **EnrollmentController** de `ms-students` recibe la petición, valida el DTO (`@Valid`) y llama a `enrollmentService.enrollStudent(1L, 1L)`.

4. **EnrollmentServiceImpl** ejecuta:
   - Consulta `studentRepository.findById(1L)` → encuentra a Camila (activa).
   - Valida `student.getActive() == true`.
   - Llama `courseClient.reserveSlot(1L)`.

5. **CourseClient (Feign)**:
   - Consulta Eureka: *"¿Dónde está `ms-courses`?"*.
   - Eureka responde: *"En 192.168.1.10:8081"*.
   - Feign envía `POST http://192.168.1.10:8081/api/courses/1/reserve`.

6. **CourseController** de `ms-courses` recibe la petición y llama a `courseService.reserveSlot(1L)`.

7. **CourseServiceImpl**:
   - Busca curso id=1 (JAVA-101).
   - Verifica `availableSlots > 0` (30 cupos disponibles).
   - Decrementa: `availableSlots = 29`.
   - Guarda en H2.
   - Retorna 200 OK.

8. **CourseClient** recibe 200 OK → no lanza excepción.

9. **EnrollmentServiceImpl**:
   - Crea entidad `Enrollment(studentId=1, courseId=1, status=ACTIVE)`.
   - La persiste en H2.
   - Retorna `EnrollmentResponseDTO`.

10. **EnrollmentController** retorna `201 Created` con el cuerpo de la matrícula.

11. **API Gateway** reenvía la respuesta al cliente externo.

## Flujo de Matrícula con Estudiante Inactivo

```http
POST http://localhost:8080/api/enrollments
{"studentId": 3, "courseId": 1}    # Laura, inactiva
```

1. EnrollmentController recibe la petición.
2. Service consulta a Laura → existe.
3. Valida `active`: **es false**.
4. Lanza `InactiveStudentException(3L)`.
5. `GlobalExceptionHandler` la captura y retorna **409 Conflict**.
6. **NUNCA se llama a `ms-courses`** (eficiencia: fallar rápido).
7. **NUNCA se persiste la matrícula** (consistencia).

## Flujo de Matrícula Sin Cupos

```http
# Si JAVA-101 ya tiene availableSlots = 0
POST http://localhost:8080/api/enrollments
{"studentId": 1, "courseId": 1}
```

1. EnrollmentController recibe la petición.
2. Service consulta a Camila → existe y está activa.
3. Llama `courseClient.reserveSlot(1L)`.
4. `ms-courses` detecta `availableSlots = 0`.
5. Lanza `NoSlotsAvailableException(1L)` → su `GlobalExceptionHandler` retorna **409 Conflict**.
6. Feign en `ms-students` recibe 409 → lanza `FeignException` con `status = 409`.
7. El `GlobalExceptionHandler` de `ms-students` captura `FeignException` y mapea 409 remoto → 409 local.
8. El cliente externo recibe **409 Conflict**.
9. **La matrícula NUNCA se persiste**.

## Flujo de Cancelación de Matrícula

```http
DELETE http://localhost:8080/api/enrollments/1
```

1. EnrollmentController recibe la petición.
2. Service busca matrícula id=1.
3. Cambia `status` de `ACTIVE` a `CANCELLED`.
4. Llama `courseClient.releaseSlot(1L)`.
5. `ms-courses` incrementa `availableSlots` (de 29 a 30).
6. Service guarda la matrícula actualizada en H2.
7. Retorna **200 OK** con el estado `CANCELLED`.

# Pruebas Manuales con Postman

## Importar la Colección

1. Abrir **Postman**.
2. Clic en **Import** (arriba a la izquierda).
3. Arrastrar el archivo `postman/UniRemington.postman_collection.json` o seleccionarlo con **Choose Files**.
4. La colección **UniRemington API** aparecerá en el panel izquierdo con 4 carpetas:
   - 🎓 Courses
   - 👥 Students
   - 📚 Enrollments
   - 🔍 Infraestructura

## Variables de Entorno

La colección define la variable `{{gateway}}` con valor `http://localhost:8080`. Si tu Gateway corre en otro puerto, modifica esta variable.

## Secuencia de Pruebas Recomendada

### Paso 1: Verificar listados iniciales

1. **GET** `Listar todos los cursos` → 200 OK con 3 cursos.
2. **GET** `Listar todos los estudiantes` → 200 OK con 3 estudiantes (2 activos, 1 inactivo).
3. **GET** `Listar todas las matriculas` → 200 OK con lista vacía.

### Paso 2: Demostrar el happy path de matrícula

4. **POST** `Matricular estudiante activo (HAPPY PATH)` → 201 Created.
5. **GET** `Listar todos los cursos` → 200 OK, JAVA-101 ahora tiene `availableSlots: 29`.
6. **GET** `Listar todas las matriculas` → 200 OK, 1 matrícula con `status: ACTIVE`.

### Paso 3: Demostrar el caso de estudiante inactivo

7. **POST** `Matricular estudiante INACTIVO (409)` → 409 Conflict.
8. Verificar mensaje: *"El estudiante con id 3 no está activo y no puede matricularse"*.

### Paso 4: Demostrar el caso de estudiante inexistente

9. **POST** `Matricular estudiante INEXISTENTE (404)` → 404 Not Found.

### Paso 5: Cancelación

10. **DELETE** `Cancelar matricula` → 200 OK con `status: CANCELLED`.
11. **GET** `Listar todos los cursos` → JAVA-101 vuelve a tener `availableSlots: 30`.

### Paso 6: Demostrar validaciones

12. **POST** `Crear estudiante con email invalido` → 400 Bad Request.
13. **POST** `Crear curso con validacion fallida` → 400 Bad Request con `details` en el cuerpo.

### Paso 7: Demostrar infraestructura

14. **GET** `Dashboard Eureka` → ver servicios registrados.
15. **GET** `Rutas activas del Gateway` → ver las 3 rutas configuradas.
16. **GET** `Swagger UI - ms-courses` → ver documentación interactiva.

## 21.4 Casos Adicionales para Demostración

### Crear un curso nuevo

**POST** `Crear curso`:

```json
{
    "code": "REACT-401",
    "name": "React Frontend Development",
    "description": "Construcción de SPAs con React 18",
    "availableSlots": 25
}
```

### Crear un estudiante nuevo

**POST** `Crear estudiante`:

```json
{
    "firstName": "Diego",
    "lastName": "Hernández",
    "email": "diego.hernandez@uniremington.edu.co",
    "active": true
}
```

### Activar un estudiante inactivo

**PATCH** `/api/students/3/status?active=true` → Laura ahora puede matricularse.

# Glosario Técnico

**API Gateway:** Punto único de entrada perimetral que centraliza el enrutamiento HTTP hacia los microservicios internos.

**Bean Validation:** Estándar de Jakarta EE (anteriormente JSR-380) que permite declarar reglas de validación en POJOs mediante anotaciones.

**BOM (Bill of Materials):** Archivo POM importado para gestionar versiones de un conjunto de dependencias relacionadas (ej. Spring Cloud BOM).

**Cliente Feign:** Cliente HTTP declarativo de Spring Cloud. Permite consumir APIs REST mediante interfaces anotadas, sin escribir código de bajo nivel.

**Cobertura de código:** Métrica que indica qué porcentaje del código fuente se ejecuta durante las pruebas.

**DTO (Data Transfer Object):** Objeto plano usado para transferir datos entre capas o entre servicios, sin lógica de negocio.

**Eureka:** Servidor de Service Discovery de Netflix, integrado en Spring Cloud.

**FeignException:** Excepción lanzada por Feign cuando una llamada remota responde con un código HTTP de error.

**H2 Database:** Base de datos relacional escrita en Java que puede ejecutarse en memoria.

**Hibernate:** Implementación de JPA. Spring Data JPA lo usa por defecto.

**JaCoCo:** Java Code Coverage. Herramienta que mide la cobertura de tests.

**JPA (Java Persistence API):** Especificación estándar de Java para mapear objetos a tablas relacionales (ORM).

**JUnit:** Framework de pruebas unitarias estándar en Java. La versión 6 (Jupiter) es la actual.

**Maven Multi-Módulo:** Proyecto Maven con un POM padre que agrupa varios módulos hijos.

**Mockito:** Framework para crear objetos simulados (mocks) en pruebas unitarias.

**MockMvc:** Componente de Spring Test que permite probar controllers sin levantar un servidor HTTP real.

**OpenAPI:** Estándar para describir APIs REST de forma estructurada. Antes conocido como Swagger Specification.

**OpenFeign:** Implementación de Feign integrada con Spring Cloud.

**ORM (Object-Relational Mapping):** Técnica para mapear objetos Java a tablas SQL.

**POM (Project Object Model):** Archivo de configuración principal de Maven (`pom.xml`).

**Service Discovery:** Mecanismo por el cual los servicios encuentran las direcciones físicas de otros servicios dinámicamente.

**Spring Boot:** Framework que simplifica la creación de aplicaciones Spring con autoconfiguración y servidores embebidos.

**Spring Cloud:** Suite de herramientas para construir sistemas distribuidos: Service Discovery, Gateway, OpenFeign, etc.

**Spring Cloud Gateway:** Gateway reactivo basado en WebFlux.

**Spring Cloud LoadBalancer:** Cliente de balanceo de carga, reemplazo moderno de Netflix Ribbon.

**Spring Data JPA:** Módulo de Spring que simplifica el acceso a datos mediante repositorios derivados de interfaces.

**Standalone MockMvc:** Variante de MockMvc que no carga el contexto de Spring; los tests son más rápidos y unitarios.

**Swagger UI:** Interfaz web interactiva para visualizar y probar APIs OpenAPI.

**WebFlux:** Stack reactivo de Spring, alternativa no bloqueante a Spring MVC.

**@RestControllerAdvice:** Anotación que marca una clase como manejador global de excepciones para todos los `@RestController`.

---

## 👤 Autor

---
Proyecto académico desarrollado para el curso **Lenguaje de Programación Avanzado II**.
Alejandro Sanchez Acevedo
Diego Alejandro 
Uniremington -Manizales · 2026.

---
