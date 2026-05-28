# 🐍 Review Service - Microservicio en Python

## 📝 Descripción

**Review Service** es un microservicio escrito en **Python** que demuestra que los microservicios pueden estar escritos en **diferentes lenguajes** y aun así comunicarse entre sí.

Este servicio:
- ✅ Gestiona reseñas de usuarios sobre productos
- ✅ Se conecta a **User Service** (Java, puerto 8081)
- ✅ Se conecta a **Product Service** (Java, puerto 8082)
- ✅ Expone API REST en puerto 9090
- ✅ Usa **FastAPI** (framework moderno de Python)

---

## 🏗️ Arquitectura

```
┌─────────────────────────────┐
│  Review Service (Python)    │ ← Tú estás aquí
│  Puerto 9090                │
│  FastAPI                    │
└────────┬────────────────────┘
         │ HTTP Calls
         ├── GET /api/users/1 (User Service - Java)
         └── GET /api/products/1 (Product Service - Java)
```

---

## 🚀 Instalación y Ejecución

### Paso 1: Instalar dependencias

```bash
cd review-service
pip install -r requirements.txt
```

**Dependencias necesarias:**
- `FastAPI` - Framework web moderno
- `uvicorn` - Servidor ASGI (como Tomcat para Java)
- `httpx` - Cliente HTTP asincrónico
- `pydantic` - Validación de datos
- `requests` - Cliente HTTP sencillo

### Paso 2: Asegurar que servicios Java estén corriendo

Antes de iniciar Review Service, necesitas tener corriendo:
- ✅ Eureka Server (puerto 8761)
- ✅ User Service (puerto 8081)
- ✅ Product Service (puerto 8082)

```bash
# Terminal 1: Eureka
cd eureka-server && mvn spring-boot:run

# Terminal 2: User Service
cd user-service && mvn spring-boot:run

# Terminal 3: Product Service
cd product-service && mvn spring-boot:run
```

### Paso 3: Ejecutar Review Service

```bash
python main.py

# Salida esperada:
# ╔════════════════════════════════════════════════════════════╗
# ║     ✅ REVIEW SERVICE (PYTHON) INICIADO EN PUERTO 9090    ║
# ╚════════════════════════════════════════════════════════════╝
```

---

## 📚 Endpoints REST

### 1️⃣ GET - Obtener todas las reseñas
```bash
curl http://localhost:9090/api/reviews

RESPUESTA:
[
  {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "rating": 5,
    "comment": "Excelente producto!",
    "createdAt": "2024-05-28T10:35:00"
  }
]
```

### 2️⃣ GET - Obtener reseña por ID
```bash
curl http://localhost:9090/api/reviews/1

RESPUESTA:
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "rating": 5,
  "comment": "Excelente producto!",
  "createdAt": "2024-05-28T10:35:00"
}
```

### 3️⃣ GET - Reseñas de un producto
```bash
curl http://localhost:9090/api/reviews/product/1

RESPUESTA:
[
  {
    "id": 1,
    "userId": 1,
    "productId": 1,
    "rating": 5,
    "comment": "Excelente producto!",
    "createdAt": "2024-05-28T10:35:00"
  }
]
```

### 4️⃣ POST - Crear reseña ⭐ (IMPORTANTE)

Este es el endpoint que demuestra la **comunicación inter-microservicios**.

```bash
curl -X POST http://localhost:9090/api/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "rating": 5,
    "comment": "Excelente producto, muy recomendado"
  }'

PROCESO INTERNO:
1️⃣ Validar usuario en User Service (HTTP GET)
   GET http://localhost:8081/api/users/1
   
2️⃣ Validar producto en Product Service (HTTP GET)
   GET http://localhost:8082/api/products/1
   
3️⃣ Validar rating (1-5)

4️⃣ Guardar localmente en Python

RESPUESTA (201 CREATED):
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "rating": 5,
  "comment": "Excelente producto, muy recomendado",
  "createdAt": "2024-05-28T10:35:00"
}
```

### 5️⃣ PUT - Actualizar reseña
```bash
curl -X PUT http://localhost:9090/api/reviews/1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "rating": 4,
    "comment": "Bueno, pero podría mejorar"
  }'
```

### 6️⃣ DELETE - Eliminar reseña
```bash
curl -X DELETE http://localhost:9090/api/reviews/1

RESPUESTA: 204 NO CONTENT (sin cuerpo)
```

---

## 📖 Documentación Interactiva

FastAPI genera **automáticamente** documentación Swagger interactiva.

```
http://localhost:9090/docs
```

**Abre esta URL en el navegador y podrás:**
- Ver todos los endpoints
- Probar cada uno sin usar curl
- Ver el esquema de requests/responses
- Descargar especificación OpenAPI

---

## 🔄 Flujo Detallado: Crear Reseña

```
CLIENTE
│
│ POST /api/reviews
│ {
│   "userId": 1,
│   "productId": 1,
│   "rating": 5,
│   "comment": "Excelente"
│ }
│
▼
┌──────────────────────────────────┐
│  Review Service (Python)         │
│  main.py - create_review()       │
└────────────┬─────────────────────┘
             │
    ┌────────┴──────────┐
    │                   │
    ▼                   ▼
1️⃣ Validar Usuario   3️⃣ Validar Producto
   HTTP GET           HTTP GET
   User Service       Product Service
   (Java, 8081)       (Java, 8082)
   │                  │
   ▼                  ▼
✅ User exists    ✅ Product exists
   │                  │
   └────────┬─────────┘
            │
            ▼
2️⃣ Validar Rating (1-5)
   │
   ▼ ✅ Rating válido
4️⃣ Guardar localmente
   │
   ▼
✨ Reseña creada exitosamente
   │
   ▼
CLIENTE recibe respuesta 201 (CREATED)
```

---

## 💻 Comparación: Python vs Java

| Aspecto | Python (FastAPI) | Java (Spring Boot) |
|---------|-----------------|------------------|
| **Framework** | FastAPI | Spring Boot |
| **Servidor** | Uvicorn (ASGI) | Tomcat (Servlet) |
| **Validación** | Pydantic | Jakarta Validation |
| **ORM** | SQLAlchemy (opcional) | Hibernate/JPA |
| **Documentación** | Automática (Swagger) | Swagger2Doc |
| **Async** | Nativo (async/await) | Desde Spring 5+ |
| **Velocidad** | ⚡ Muy rápido | ⚡ Rápido |

---

## 🔗 Integración con Servicios Java

### Llamar a User Service desde Python

```python
async def get_user_from_java(user_id: int):
    # Esta función hace HTTP call a User Service
    url = f"http://localhost:8081/api/users/{user_id}"
    
    async with httpx.AsyncClient() as client:
        response = await client.get(url)
        
    if response.status_code == 200:
        return UserDto(**response.json())
    else:
        raise HTTPException(status_code=404)
```

**¡Es exactamente lo que hace OrderService en Java!**

```java
// Java - Spring Boot
UserDto user = restTemplate.getForObject(
    "http://user-service/api/users/" + userId,
    UserDto.class
);
```

---

## 🧪 Ejemplos Completos

### Ejemplo 1: Crear usuario y producto en Java, luego reseña en Python

```bash
# 1️⃣ Crear usuario en User Service (Java)
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Juan","email":"juan@example.com"}'
# RESPUESTA: { "id": 1, "name": "Juan", ... }

# 2️⃣ Crear producto en Product Service (Java)
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop","price":1500,"stock":10}'
# RESPUESTA: { "id": 1, "name": "Laptop", ... }

# 3️⃣ Crear reseña en Review Service (Python)
# Review Service validará usuario y producto en Java
curl -X POST http://localhost:9090/api/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "rating": 5,
    "comment": "Excelente!"
  }'
# RESPUESTA: { "id": 1, "userId": 1, "productId": 1, ... }
```

### Ejemplo 2: Obtener reseñas de un producto con datos enriquecidos

```python
# Desde Python, puedo:
# 1. Obtener reseñas del producto
# 2. Para cada reseña, obtener datos del usuario en Java
# 3. Para cada reseña, obtener datos del producto en Java
# 4. Retornar reseñas enriquecidas

reviews = await get_reviews_by_product(product_id=1)
# Puedo enriquecer cada reseña llamando a servicios Java
```

---

## 📊 Ventajas de Microservicios Poliglota

Esta arquitectura demuestra:

✅ **Libertad de lenguaje**
- User Service: Java
- Product Service: Java
- Order Service: Java
- Review Service: Python (✨ ¡diferente lenguaje!)

✅ **Comunicación vía HTTP**
- No importa el lenguaje
- Todos hablan REST/JSON

✅ **Escalabilidad independiente**
- Review Service en Python es muy ligero
- Puedo tener 100 instancias fácilmente

✅ **Equipo multinacional**
- Equipo 1: Java
- Equipo 2: Python
- Ambos trabajan independientemente

---

## 🐛 Troubleshooting

### Error: "Connection refused"
```
❌ Error: [Errno 111] Connection refused
```

**Solución:** Asegurate que User Service y Product Service estén corriendo

### Error: "Usuario no encontrado"
```
❌ Error comunicándose con User Service
```

**Solución:** Verificar que User Service tiene el usuario con ese ID

### FastAPI no inicia
```
❌ Address already in use
```

**Solución:** El puerto 9090 ya está en uso. Cambia el puerto en `main.py`:
```python
REVIEW_SERVICE_PORT = 9091  # Cambiar a otro puerto
```

---

## 📚 Recursos Adicionales

- **FastAPI Docs:** https://fastapi.tiangolo.com/
- **Pydantic:** https://docs.pydantic.dev/
- **HTTPX (HTTP Client):** https://www.python-httpx.org/
- **Uvicorn:** https://www.uvicorn.org/

---

## 🎓 Lo que aprendiste

✅ Los microservicios pueden estar en **diferentes lenguajes**
✅ Se comunican vía **HTTP/REST**
✅ La validación en un servicio llama a otros servicios
✅ Python es tan bueno como Java para microservicios
✅ FastAPI es tan rápido como Spring Boot
✅ Pydantic es tan potente como Jakarta Validation

---

**¡Felicidades! Ahora tienes un ecosistema de microservicios POLIGLOTA!**

```
Java ☕ + Python 🐍 = ❤️
```
