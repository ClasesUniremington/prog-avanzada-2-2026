# Monolith Modular Application

Aplicación monolítica MVC completa con toda la lógica mezclada, diseñada para ser posteriormente refactorizada hacia microservicios usando el patrón **Strangler Fig**.

## 🛠️ Stack Tecnológico

- **Lenguaje:** Java 21
- **Framework:** Spring Boot 4.x
- **Constructor:** Maven
- **Base de Datos:** H2 Database (en memoria)
- **Pruebas:** JUnit 6 + Mockito
- **Cobertura:** JaCoCo (umbral mínimo: 80%)
- **API Documentation:** Springdoc OpenAPI 3

## 📂 Estructura del Proyecto (Monolito Único)

```
monolith-modular/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/co/edu/uniremington/
    │   │   ├── MonolithApplication.java
    │   │   ├── controller/        (Todos los controladores)
    │   │   │   ├── ProductController.java
    │   │   │   ├── UserController.java
    │   │   │   └── OrderController.java
    │   │   ├── service/           (Toda la lógica de negocio)
    │   │   │   ├── ProductService.java
    │   │   │   ├── ProductServiceImpl.java
    │   │   │   ├── UserService.java
    │   │   │   ├── UserServiceImpl.java
    │   │   │   ├── OrderService.java
    │   │   │   └── OrderServiceImpl.java
    │   │   ├── repository/        (Todos los repositorios)
    │   │   │   ├── ProductRepository.java
    │   │   │   ├── UserRepository.java
    │   │   │   └── OrderRepository.java
    │   │   ├── model/             (Todas las entidades)
    │   │   │   ├── Product.java
    │   │   │   ├── User.java
    │   │   │   ├── Order.java
    │   │   │   └── OrderItem.java
    │   │   └── dto/               (Transfer Objects)
    │   │       ├── OrderItemDto.java
    │   │       ├── OrderRequest.java
    │   │       └── OrderResponse.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/co/edu/uniremington/
            └── service/
                ├── ProductServiceImplTest.java
                └── OrderServiceImplTest.java
```

## 🚀 Características

### Todo en un solo paquete `co.edu.uniremington`
- **controller/** - 3 controladores REST (Product, User, Order)
- **service/** - 6 servicios (interfaces + implementaciones)
- **repository/** - 3 repositorios JPA
- **model/** - 4 entidades (Product, User, Order, OrderItem)
- **dto/** - 3 DTOs de transferencia

### Entidades y Funcionalidad

**Product** (Productos)
- CRUD completo
- Control de stock
- Validaciones de precio y cantidad

**User** (Usuarios)
- CRUD completo
- Email único
- Datos de contacto

**Order** (Órdenes)
- Relación 1-a-N con OrderItem
- Validación de usuario existente
- Decremento automático de stock
- Fecha de creación automática

## 🔌 Endpoints REST

### Productos
```
GET    /api/products          - Listar todos
GET    /api/products/{id}     - Obtener por ID
POST   /api/products          - Crear
PUT    /api/products/{id}     - Actualizar
DELETE /api/products/{id}     - Eliminar
```

### Usuarios
```
GET    /api/users             - Listar todos
GET    /api/users/{id}        - Obtener por ID
POST   /api/users             - Crear
PUT    /api/users/{id}        - Actualizar
DELETE /api/users/{id}        - Eliminar
```

### Órdenes
```
GET    /api/orders            - Listar todas
GET    /api/orders/{id}       - Obtener por ID
GET    /api/orders/user/{userId} - Órdenes de usuario
POST   /api/orders            - Crear
DELETE /api/orders/{id}       - Eliminar
```

## 🚀 Cómo Ejecutar

### Requisitos
- JDK 21+
- Maven 3.8+

### Compilación y Pruebas
```bash
cd monolith-modular
mvn clean test
```

### Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

La aplicación estará en `http://localhost:8080`

### Documentación Swagger
Una vez ejecutada: `http://localhost:8080/swagger-ui.html`

### H2 Console
`http://localhost:8080/h2-console`

## ✅ Cobertura de Código

JaCoCo requiere **80% de cobertura mínima**:

```bash
mvn clean test
# Si falla: check target/site/jacoco/index.html
```

## 🔄 Transición a Microservicios

Este monolito está diseñado para migración fácil:

1. **Fase 1 (Actual):** Monolito con todo mezclado
2. **Fase 2:** Extraer módulos a carpetas separadas (catalog/, order/, user/)
3. **Fase 3:** Convertir a servicios independientes con BD separadas
4. **Fase 4:** Comunicación vía HTTP/REST o mensajería

## 📝 Principios SOLID Aplicados

✅ **Single Responsibility:** Cada clase tiene una sola responsabilidad  
✅ **Open/Closed:** Extensible mediante interfaces  
✅ **Liskov Substitution:** Implementaciones intercambiables  
✅ **Interface Segregation:** Interfaces específicas  
✅ **Dependency Inversion:** Inyección por constructor  

## 🧪 Tests Incluidos

- **ProductServiceImplTest** - Cobertura de ProductService
- **OrderServiceImplTest** - Cobertura de OrderService con validaciones cruzadas

## 📚 Referencias

- [Spring Boot 4.x](https://spring.io/projects/spring-boot)
- [Springdoc OpenAPI 3](https://springdoc.org/)
- [JUnit 6](https://junit.org/junit5/)
- [JaCoCo](https://www.eclemma.org/jacoco/)
- [Clean Code](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
