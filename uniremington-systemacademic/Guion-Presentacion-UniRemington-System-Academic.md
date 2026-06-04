# 🎤 Guion Final de Presentación — UniRemington System Academic

### Duración objetivo: 10–15 minutos

---

## ⏱️ Minuto 0–1:30 — Apertura y Contexto

> "Buenos días. Voy a presentar **UniRemington System Academic**, un sistema académico distribuido construido con arquitectura de microservicios. El sistema gestiona tres dominios: **estudiantes**, **cursos** y **matrículas**.
>
> El reto central que resuelve es la **orquestación de una transacción distribuida**: cuando matriculamos a un estudiante, el cupo del curso —que vive en *otro* microservicio— debe reservarse de forma coherente. Si esa reserva falla, la matrícula no debe quedar registrada. Eso es lo que hace interesante este proyecto."

---

## ⏱️ Minuto 1:30–4:00 — Arquitectura

*(Mostrar el diagrama del README)*

> "**UniRemington System Academic** tiene **cuatro microservicios independientes**:
>
> - **Eureka Server** es el *Service Discovery*: el directorio donde cada servicio se registra dinámicamente al arrancar.
> - **API Gateway** es el único punto de entrada. El cliente externo nunca habla directamente con los servicios de negocio; todo pasa por aquí.
> - **MS-Courses** administra el catálogo y controla los cupos.
> - **MS-Students** administra estudiantes y **orquesta** las matrículas.
>
> Cada microservicio aplica el patrón **MVC clásico**: Controller → Service → Repository → Domain. Y cada uno tiene su **propia base de datos H2**, porque en microservicios cada servicio es dueño de sus datos.
>
> La comunicación entre `ms-students` y `ms-courses` se hace con **OpenFeign**: `ms-students` no conoce la dirección física de `ms-courses`, solo su nombre lógico, y Eureka lo resuelve en tiempo de ejecución.
>
> Todo el código está escrito **100% en inglés** —clases, métodos, variables y tablas—, dejando solo los comentarios y mensajes de excepción en español, tal como permite el enunciado.
>
> "Quiero destacar **qué principios SOLID apliqué concretamente**:
>
> - **S — Responsabilidad Única:** cada clase tiene una sola razón para cambiar. El Controller solo maneja HTTP, el Service solo lógica de negocio, el Repository solo acceso a datos, y el Mapper solo conversión entre entidad y DTO.
> - **O — Abierto/Cerrado:** gracias al manejador global de excepciones, puedo agregar nuevos tipos de error sin modificar los controllers existentes.
> - **L — Sustitución de Liskov:** los repositorios extienden `JpaRepository`, y cualquier implementación de Spring Data respeta ese contrato sin romper el código.
> - **I — Segregación de Interfaces:** cada servicio tiene su propia interfaz enfocada —`CourseService`, `StudentService`, `EnrollmentService`— en lugar de una interfaz gigante con todo mezclado.
> - **D — Inversión de Dependencias:** y este es el más visible. Cada servicio se divide en **interfaz e implementación**. Los controllers dependen de la interfaz `CourseService`, no de `CourseServiceImpl`. Esto permite inyectar mocks en las pruebas y cambiar la implementación sin tocar el resto del sistema."
>
> **Una nota sobre los puertos:** el documento sugiere 8761, 8080, 8081 y 8082, pero los configuré como **18761, 18080, 18081 y 18082**. Lo hice deliberadamente para **evitar conflictos** con servicios que comúnmente ocupan los puertos estándar —por ejemplo, el 8080 lo usan muchas aplicaciones por defecto—. Además, los dejé **parametrizados con variables de entorno**, así que en cualquier máquina se pueden ajustar sin tocar el código, simplemente definiendo una variable."

---

## ⏱️ Minuto 4:00–6:00 — Stack y Decisión Técnica Clave

> "El stack es **Java 21, Spring Boot 4, Spring Cloud, H2, JPA, OpenFeign, JUnit 6, Mockito y JaCoCo**.
>
> "Quiero detenerme en **dos elecciones tecnológicas clave**:
>
> **Primero, OpenFeign para la comunicación entre microservicios.** OpenFeign es un cliente HTTP **declarativo**: en lugar de escribir manualmente el código para construir peticiones, parsear respuestas y manejar conexiones, simplemente declaro una **interfaz con anotaciones** y Spring genera la implementación automáticamente. Por ejemplo, mi `CourseClient` declara `reserveSlot` y `releaseSlot` como métodos de una interfaz, y Feign los convierte en llamadas HTTP reales. Lo elegí por tres razones: se **integra nativamente con Eureka**, así que uso el nombre lógico `ms-courses` en vez de una IP fija; incluye **balanceo de carga** automático si hubiera varias instancias; y produce un código **mucho más limpio y legible** que la alternativa tradicional, `RestTemplate`, que obliga a escribir mucho código repetitivo.
>
> **Segundo, Spring Data JPA para la persistencia.** JPA es el estándar de Java para mapear objetos a tablas relacionales —lo que se llama ORM, Object-Relational Mapping—. Con Spring Data JPA, solo declaro una interfaz que extiende `JpaRepository` y automáticamente obtengo las operaciones CRUD: guardar, buscar, listar, eliminar, sin escribir una sola línea de SQL. Y si necesito una consulta personalizada, basta con declarar el método siguiendo una convención de nombres, como `findByEmail`, y Spring genera la consulta. Lo elegí porque **elimina el código repetitivo de acceso a datos**, me deja concentrarme en la lógica de negocio, y porque al usar el estándar JPA podría migrar de H2 a PostgreSQL o MySQL **sin cambiar el código**, solo la configuración.
>
> Ambas tecnologías comparten la misma filosofía: **declaro qué quiero, no cómo hacerlo**, y el framework se encarga del resto."
>
> "Y quiero destacar una **decisión técnica importante**: el documento pedía Spring Cloud 2024.0.0, pero esa versión está basada en Spring Boot 3.4 y es **incompatible con Spring Boot 4**, que usa Spring Framework 7. Para honrar el requisito principal —Spring Boot 4— actualicé Spring Cloud al release alineado oficialmente, **2025.1.0**. Esto demuestra que no apliqué las versiones mecánicamente, sino que validé su compatibilidad real."

---

## ⏱️ Minuto 6:00–10:00 — Demo en Vivo

*(Tener los 4 servicios arrancados de antemano)*

### Paso 1 — Eureka y Gateway

*(navegador http://localhost:18761)*

> "Aquí en el dashboard de Eureka están los tres servicios registrados dinámicamente, todos en estado UP, incluyendo el **API Gateway**.
>
> Quiero recalcar algo: **todas las peticiones de la demo pasan por el Gateway en el puerto 18080**, nunca directamente a los microservicios. El Gateway resuelve a qué servicio enrutar consultando a Eureka. Por eso uso siempre la URL del Gateway en Postman."

### Paso 2 — CRUD de Cursos

*(Postman)*

> "Primero demuestro el CRUD de cursos. Listo los cursos iniciales... creo uno nuevo..."

```json
POST /api/courses  { "code": "MATH-101", "name": "Cálculo I", "availableSlots": 25 }  → 201 Created
```

> "...lo actualizo..."

```json
PUT /api/courses/4  { "code": "MATH-101", "name": "Cálculo Diferencial", "availableSlots": 30 }  → 200 OK
```

> "...y confirmo que el CRUD completo funciona a través del Gateway."

### Paso 3 — Estudiantes

*(Postman)*

> "Listo los estudiantes iniciales. Noten que tenemos un estudiante **inactivo**, Laura, que usaré para demostrar una validación de negocio."

### Paso 4 — Matrícula exitosa (happy path)

> "Matriculo a Camila, que está activa, en Java-101."

```json
POST /api/enrollments  { "studentId": 1, "courseId": 1 }  → 201 Created
```

> "Y si consulto el curso, los cupos bajaron de 30 a 29. Eso confirma que `ms-students` llamó remotamente a `ms-courses` vía Feign para reservar el cupo. **Esa es la orquestación distribuida en acción.**"

### Paso 5 — Estudiante inactivo (regla de negocio)

```json
POST /api/enrollments  { "studentId": 3, "courseId": 1 }  → 409 Conflict
```

> "El sistema valida el estado **antes** de llamar a `ms-courses`. Falla rápido y no desperdicia la llamada remota."

### Paso 6 — Cancelación

```json
DELETE /api/enrollments/1  → 200 OK (status: CANCELLED)
```

> "Cambia el estado a CANCELLED y libera el cupo: vuelve a 30. Además, si intento cancelar dos veces, lanza una excepción que **evita liberar el cupo por duplicado**."

---

## ⏱️ Minuto 10:00–12:30 — Calidad y Testing

*(Terminal + reporte JaCoCo)*

> "El PDF exige cobertura mínima del 80%. Ejecuto `mvn verify`...
>
> El build pasa con **83 pruebas, cero fallos**. La cobertura es **96.5% en ms-courses y 94.4% en ms-students** — muy por encima del mínimo.
>
> Lo importante es **cómo** están hechos los tests, siguiendo las reglas estrictas del documento: están **prohibidos** `@SpringBootTest` y la base de datos H2 en las pruebas. Todo se simula con Mockito usando `@Mock` e `@InjectMocks`. Los tests de controlador usan MockMvc en modo standalone, sin levantar el contexto de Spring.
>
> El test más importante es este: verifica que **si la reserva remota falla, la matrícula nunca se persiste**. Eso certifica la consistencia del sistema distribuido."

---

## ⏱️ Minuto 12:30–14:30 — Manejo de Errores y Documentación

> "Para el manejo de errores usé una clase llamada `GlobalExceptionHandler`, **una en cada microservicio**, ubicada en el paquete `exception` —por ejemplo, en `com.uniremington.courses.exception` para cursos y `com.uniremington.students.exception` para estudiantes—.
>
> Esta clase está anotada con **`@RestControllerAdvice`**. Voy a explicar qué hace y por qué la usé.
>
> `@RestControllerAdvice` es una anotación de Spring que convierte una clase en un **interceptor global de excepciones** para todos los controllers del microservicio. La idea es esta: en lugar de llenar cada método de los controllers con bloques `try-catch` repetidos, **centralizo todo el manejo de errores en un único lugar**.
>
> Funciona así: cuando cualquier controller lanza una excepción —por ejemplo, `CourseNotFoundException`—, Spring **intercepta esa excepción antes de que llegue al cliente** y la redirige al método correspondiente dentro del `GlobalExceptionHandler`, que está marcado con `@ExceptionHandler`. Ese método decide qué **código HTTP** y qué **cuerpo de respuesta** devolver.
>
> Las ventajas son tres:
>
> - **Centralización:** toda la lógica de errores está en un solo archivo, fácil de mantener.
> - **Consistencia:** todas las respuestas de error tienen el mismo formato JSON —con timestamp, código, mensaje y ruta— gracias a una clase `ErrorResponse` compartida.
> - **Controllers limpios:** los controllers solo se ocupan del camino feliz; no se ensucian con manejo de errores.
>
> En el proyecto, cada `GlobalExceptionHandler` traduce las excepciones a códigos HTTP coherentes: un `CourseNotFoundException` da 404, un `NoSlotsAvailableException` da 409, y los errores de validación dan 400.
>
> Y un detalle especialmente elegante: cuando `ms-courses` responde con un error, Feign lo recibe como una `FeignException` en `ms-students`. Mi handler **captura esa `FeignException` y la mapea al mismo código HTTP** para el cliente final. Así, sin importar dónde se originó el error en la cadena de microservicios, el cliente recibe una respuesta coherente y predecible.
>
> En cuanto a documentación: el proyecto incluye un **README completo**, cada microservicio expone su documentación interactiva con **Swagger/OpenAPI**, y entrego una **colección de Postman** lista para probar todos los flujos. El código está organizado en paquetes por capa —controller, service, repository, domain, dto, mapper, exception— lo que lo hace fácil de navegar y mantener."

---

## ⏱️ Minuto 14:30–15:00 — Cierre

> "En resumen: **UniRemington System Academic** implementa una arquitectura de microservicios profesional con Spring Cloud, aplica el patrón MVC clásico, orquesta transacciones distribuidas con consistencia garantizada, mantiene el código 100% en inglés siguiendo principios SOLID, y certifica su calidad con una cobertura de pruebas superior al 94%.
>
> El proyecto cumple los seis criterios de evaluación del documento. Quedo atento a sus preguntas."

---

## 💬 Preguntas Probables del Docente

**"¿Por qué cambiaste los puertos del enunciado?"**

> "Para garantizar portabilidad. El puerto 8080 es uno de los más ocupados en cualquier equipo de desarrollo —Tomcat, otros proyectos Spring, herramientas locales—. Usar el rango 18xxx reduce a casi cero la probabilidad de conflicto al ejecutar el proyecto en cualquier dispositivo. Y como están parametrizados con variables de entorno como `${MS_COURSES_PORT:18081}`, si se requieren los puertos exactos del enunciado, basta con definir esas variables sin recompilar nada."

**"¿Por qué OpenFeign y no RestTemplate?"**

> "Feign es declarativo: defino una interfaz con anotaciones y Spring genera la implementación. Se integra nativamente con Eureka y el balanceador, y usa nombres lógicos en lugar de URLs físicas. RestTemplate funcionaría, pero obliga a escribir mucho más código repetitivo."

**"¿Qué pasa si ms-courses se cae justo después de reservar el cupo?"**

> "Quedaría un cupo reservado sin matrícula. En producción se resolvería con una transacción de compensación, el patrón Saga. Para el alcance académico, la orquestación síncrona con validación de respuesta es suficiente."

**"¿Por qué los tests de servicio no usan H2?"**

> "El PDF lo prohíbe, y tiene fundamento: si dependen de H2 dejan de ser unitarios y se vuelven de integración. Mockito permite aislar la lógica de negocio por completo, haciendo los tests rápidos y reproducibles."

**"¿Cómo garantizas la consistencia distribuida?"**

> "Con orquestación: `ms-students` coordina la secuencia —valida, reserva, persiste— y solo confirma cambios locales si la operación remota tuvo éxito. Y la validación de doble cancelación evita liberar cupos por duplicado."

---

## 📊 Cobertura de los Criterios de Evaluación

| Criterio (peso) | Dónde se evidencia en el guion |
|---|---|
| **Implementación Completa (20%)** | Demo: CRUD de cursos + flujos de matrícula/cancelación |
| **Automatización de Pruebas (20%)** | Minuto 10:00–12:30: 83 tests, cobertura 94-96% |
| **Integración Microservicios (15%)** | Demo Paso 1: Eureka + Gateway en vivo |
| **Código Limpio y Documentación (15%)** | SOLID, estructura por capas, README, Swagger, Postman |
| **Código en INGLÉS (10%)** | Mencionado en Arquitectura y Cierre |
| **Sustentación Oral (20%)** | Estructura completa de 15 min con cierre y preguntas |

---

## 📝 Notas para el Presentador

- **Tiempo total:** ~15 minutos, justo en el límite superior. Si en el ensayo te pasas, el bloque más fácil de recortar es la demo del CRUD de cursos (Paso 2): muestra solo el `POST` de creación y omite el `PUT`, ahorrando ~30 segundos.
- **Antes de empezar:** ten los 4 servicios arrancados y verificados en el dashboard de Eureka.
- **Backup:** lleva una copia del proyecto en USB o nube por si falla la red.
- **Postman:** ten la colección importada y la variable `{{gateway}}` apuntando a `http://localhost:18080`.
- **Orden de arranque:** Eureka → MS-Courses → MS-Students → API Gateway.
