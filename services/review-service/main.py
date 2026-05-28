"""
═══════════════════════════════════════════════════════════════════════════════
REVIEW MICROSERVICE - Python con FastAPI
═══════════════════════════════════════════════════════════════════════════════

Este microservicio demuestra que los microservicios pueden estar escritos en
DIFERENTES LENGUAJES y aun así comunicarse entre sí.

RESPONSABILIDAD:
✅ Gestionar reseñas de usuarios sobre productos
✅ Conectarse a User Service (Java, puerto 8081)
✅ Conectarse a Product Service (Java, puerto 8082)
✅ Registrarse en Eureka (Java, puerto 8761)

PUERTO: 9090
TECNOLOGÍA: Python + FastAPI (framework moderno y rápido)

VENTAJAS DE FASTAPI:
- Automáticamente genera documentación Swagger: /docs
- Es tan rápido como Node.js/Go
- Fácil de escribir y entender
- Validación automática con Pydantic

ENDPOINTS:
  GET    /api/reviews              → Todas las reseñas
  GET    /api/reviews/{id}         → Reseña específica
  GET    /api/reviews/product/{pid}→ Reseñas de un producto
  POST   /api/reviews              → Crear reseña
  PUT    /api/reviews/{id}         → Actualizar reseña
  DELETE /api/reviews/{id}         → Eliminar reseña

═══════════════════════════════════════════════════════════════════════════════
"""

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Optional
import httpx
import logging
from datetime import datetime
import os

# ═════════════════════════════════════════════════════════════════════════════
# CONFIGURACIÓN
# ═════════════════════════════════════════════════════════════════════════════

# Crear aplicación FastAPI
app = FastAPI(
    title="Review Service",
    description="Microservicio de reseñas en Python",
    version="1.0.0"
)

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# URLs de los servicios Java (descubiertos vía Eureka en producción)
# En desarrollo, usamos URLs directas
USER_SERVICE_URL = "http://localhost:8081"
PRODUCT_SERVICE_URL = "http://localhost:8082"
EUREKA_SERVER = "http://localhost:8761/eureka"

# Puerto del Review Service
REVIEW_SERVICE_PORT = 9090

# ═════════════════════════════════════════════════════════════════════════════
# MODELOS PYDANTIC (equivalente a @Entity en Java)
# ═════════════════════════════════════════════════════════════════════════════

class Review(BaseModel):
    """
    Modelo de Reseña

    Similar a @Entity en Java, pero en Python usamos Pydantic.
    Pydantic automáticamente:
    - Valida tipos
    - Serializa a JSON
    - Genera documentación Swagger
    """
    id: Optional[int] = None
    userId: int
    productId: int
    rating: int  # 1-5 estrellas
    comment: str
    createdAt: Optional[str] = None

    class Config:
        # Ejemplo de una reseña
        json_schema_extra = {
            "example": {
                "userId": 1,
                "productId": 1,
                "rating": 5,
                "comment": "Excelente producto, muy recomendado"
            }
        }


class UserDto(BaseModel):
    """DTO recibido de User Service"""
    id: int
    name: str
    email: str
    phone: Optional[str] = None


class ProductDto(BaseModel):
    """DTO recibido de Product Service"""
    id: int
    name: str
    price: float
    stock: int


# ═════════════════════════════════════════════════════════════════════════════
# BASE DE DATOS SIMULADA (En producción, usaría SQLAlchemy + PostgreSQL)
# ═════════════════════════════════════════════════════════════════════════════

# Simulamos una BD con un diccionario
reviews_db: dict[int, Review] = {}
next_id = 1

# ═════════════════════════════════════════════════════════════════════════════
# FUNCIONES PARA COMUNICARSE CON SERVICIOS JAVA
# ═════════════════════════════════════════════════════════════════════════════

async def get_user_from_java(user_id: int) -> UserDto:
    """
    Llamar a User Service (Java) para obtener datos del usuario

    ¡ESTE ES EL PUNTO CLAVE!

    En lugar de tener un modelo User en Python, llamamos a User Service
    vía HTTP. Es lo mismo que hace OrderService en Java.

    Paso a paso:
    1. Hacemos petición HTTP GET a User Service
    2. Recibimos JSON
    3. Convertimos a DTO
    4. Validamos que existe

    Args:
        user_id: ID del usuario a validar

    Returns:
        UserDto con datos del usuario

    Raises:
        HTTPException si el usuario no existe
    """
    print(f"\n👤 [Python] Validando usuario ID: {user_id}")

    try:
        # Hacer petición HTTP a User Service (Java)
        url = f"{USER_SERVICE_URL}/api/users/{user_id}"
        print(f"   → GET {url}")

        async with httpx.AsyncClient() as client:
            response = await client.get(url, timeout=5.0)

        # Si retorna 200 OK
        if response.status_code == 200:
            user_data = response.json()
            user = UserDto(**user_data)
            print(f"   ✅ Usuario encontrado: {user.name}")
            return user
        else:
            print(f"   ❌ Usuario no encontrado")
            raise HTTPException(
                status_code=404,
                detail=f"Usuario con ID {user_id} no encontrado en User Service"
            )

    except Exception as e:
        print(f"   ❌ Error: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Error comunicándose con User Service: {str(e)}"
        )


async def get_product_from_java(product_id: int) -> ProductDto:
    """
    Llamar a Product Service (Java) para obtener datos del producto

    Similar a get_user_from_java, pero llamamos a Product Service

    Args:
        product_id: ID del producto

    Returns:
        ProductDto con datos del producto
    """
    print(f"📦 [Python] Validando producto ID: {product_id}")

    try:
        url = f"{PRODUCT_SERVICE_URL}/api/products/{product_id}"
        print(f"   → GET {url}")

        async with httpx.AsyncClient() as client:
            response = await client.get(url, timeout=5.0)

        if response.status_code == 200:
            product_data = response.json()
            product = ProductDto(**product_data)
            print(f"   ✅ Producto encontrado: {product.name}")
            return product
        else:
            print(f"   ❌ Producto no encontrado")
            raise HTTPException(
                status_code=404,
                detail=f"Producto con ID {product_id} no encontrado en Product Service"
            )

    except Exception as e:
        print(f"   ❌ Error: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Error comunicándose con Product Service: {str(e)}"
        )


# ═════════════════════════════════════════════════════════════════════════════
# ENDPOINTS REST (Equivalentes a @GetMapping, @PostMapping en Java)
# ═════════════════════════════════════════════════════════════════════════════

@app.get("/", tags=["Info"])
async def root():
    """
    Endpoint raíz - Información del servicio

    Equivalente a ir a http://localhost:9090
    """
    return {
        "service": "Review Service",
        "version": "1.0.0",
        "language": "Python",
        "framework": "FastAPI",
        "port": REVIEW_SERVICE_PORT,
        "endpoints": {
            "all_reviews": "GET /api/reviews",
            "review_by_id": "GET /api/reviews/{id}",
            "reviews_by_product": "GET /api/reviews/product/{product_id}",
            "create_review": "POST /api/reviews",
            "update_review": "PUT /api/reviews/{id}",
            "delete_review": "DELETE /api/reviews/{id}",
            "api_docs": "GET /docs"  # ⭐ Documentación automática
        }
    }


@app.get("/api/reviews", tags=["Reviews"])
async def get_all_reviews() -> List[Review]:
    """
    GET /api/reviews

    Obtener todas las reseñas

    Equivalente a UserController.getAllUsers() en Java
    """
    print(f"\n📡 [Python REST] GET /api/reviews")
    print(f"   ✅ Retornando {len(reviews_db)} reseñas")
    return list(reviews_db.values())


@app.get("/api/reviews/{review_id}", tags=["Reviews"])
async def get_review_by_id(review_id: int) -> Review:
    """
    GET /api/reviews/{id}

    Obtener una reseña específica

    Equivalente a UserController.getUserById(id) en Java
    """
    print(f"\n📡 [Python REST] GET /api/reviews/{review_id}")

    if review_id not in reviews_db:
        print(f"   ❌ Reseña no encontrada")
        raise HTTPException(status_code=404, detail="Reseña no encontrada")

    print(f"   ✅ Reseña encontrada")
    return reviews_db[review_id]


@app.get("/api/reviews/product/{product_id}", tags=["Reviews"])
async def get_reviews_by_product(product_id: int) -> List[Review]:
    """
    GET /api/reviews/product/{product_id}

    Obtener todas las reseñas de un producto

    Valida que el producto existe en Product Service (Java)
    """
    print(f"\n📡 [Python REST] GET /api/reviews/product/{product_id}")

    # Validar que el producto existe en Java
    await get_product_from_java(product_id)

    # Filtrar reseñas de ese producto
    product_reviews = [r for r in reviews_db.values() if r.productId == product_id]
    print(f"   ✅ Encontradas {len(product_reviews)} reseñas")

    return product_reviews


@app.post("/api/reviews", tags=["Reviews"], status_code=201)
async def create_review(review: Review) -> Review:
    """
    POST /api/reviews

    ⭐ ENDPOINT MÁS IMPORTANTE

    Crear una nueva reseña.

    PASO A PASO (IGUAL QUE EN JAVA):
    1️⃣ Validar que el usuario existe (HTTP a User Service)
    2️⃣ Validar que el producto existe (HTTP a Product Service)
    3️⃣ Validar que el rating está entre 1-5
    4️⃣ Guardar localmente

    Body (JSON):
    {
        "userId": 1,
        "productId": 1,
        "rating": 5,
        "comment": "Excelente producto!"
    }

    Respuesta (201 CREATED):
    {
        "id": 1,
        "userId": 1,
        "productId": 1,
        "rating": 5,
        "comment": "Excelente producto!",
        "createdAt": "2024-05-28T10:35:00"
    }
    """
    global next_id

    print(f"\n📦 [Python] CREANDO RESEÑA")
    print(f"   Usuario ID: {review.userId}")
    print(f"   Producto ID: {review.productId}")
    print(f"   Rating: {review.rating}/5")

    try:
        # ═════════════════════════════════════════════════════════════
        # PASO 1: Validar usuario (HTTP call a User Service - JAVA)
        # ═════════════════════════════════════════════════════════════
        print(f"\n1️⃣ Validando usuario...")
        user = await get_user_from_java(review.userId)

        # ═════════════════════════════════════════════════════════════
        # PASO 2: Validar producto (HTTP call a Product Service - JAVA)
        # ═════════════════════════════════════════════════════════════
        print(f"\n2️⃣ Validando producto...")
        product = await get_product_from_java(review.productId)

        # ═════════════════════════════════════════════════════════════
        # PASO 3: Validar rating
        # ═════════════════════════════════════════════════════════════
        print(f"\n3️⃣ Validando rating...")
        if not (1 <= review.rating <= 5):
            print(f"   ❌ Rating debe estar entre 1-5")
            raise HTTPException(
                status_code=400,
                detail="Rating debe estar entre 1 y 5"
            )
        print(f"   ✅ Rating válido")

        # ═════════════════════════════════════════════════════════════
        # PASO 4: Guardar localmente
        # ═════════════════════════════════════════════════════════════
        print(f"\n4️⃣ Guardando reseña localmente...")
        review.id = next_id
        review.createdAt = datetime.now().isoformat()
        reviews_db[next_id] = review
        next_id += 1

        print(f"   ✅ Reseña creada con ID: {review.id}")
        print(f"\n✨ RESEÑA CREADA EXITOSAMENTE")

        return review

    except HTTPException:
        raise
    except Exception as e:
        print(f"\n❌ ERROR: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))


@app.put("/api/reviews/{review_id}", tags=["Reviews"])
async def update_review(review_id: int, review_update: Review) -> Review:
    """
    PUT /api/reviews/{id}

    Actualizar una reseña
    """
    print(f"\n📡 [Python REST] PUT /api/reviews/{review_id}")

    if review_id not in reviews_db:
        print(f"   ❌ Reseña no encontrada")
        raise HTTPException(status_code=404, detail="Reseña no encontrada")

    # Actualizar
    review = reviews_db[review_id]
    review.rating = review_update.rating
    review.comment = review_update.comment

    print(f"   ✅ Reseña actualizada")
    return review


@app.delete("/api/reviews/{review_id}", tags=["Reviews"], status_code=204)
async def delete_review(review_id: int):
    """
    DELETE /api/reviews/{id}

    Eliminar una reseña
    """
    print(f"\n📡 [Python REST] DELETE /api/reviews/{review_id}")

    if review_id not in reviews_db:
        print(f"   ❌ Reseña no encontrada")
        raise HTTPException(status_code=404, detail="Reseña no encontrada")

    del reviews_db[review_id]
    print(f"   ✅ Reseña eliminada")


@app.get("/health", tags=["System"])
async def health_check():
    """
    Health check - usado por Eureka para verificar que el servicio está vivo
    """
    return {
        "status": "UP",
        "service": "review-service",
        "timestamp": datetime.now().isoformat()
    }


# ═════════════════════════════════════════════════════════════════════════════
# MENSAJE AL INICIAR
# ═════════════════════════════════════════════════════════════════════════════

@app.on_event("startup")
async def startup_event():
    """Se ejecuta cuando inicia el servidor"""
    print("\n")
    print("╔════════════════════════════════════════════════════════════╗")
    print("║     ✅ REVIEW SERVICE (PYTHON) INICIADO EN PUERTO 9090    ║")
    print("╚════════════════════════════════════════════════════════════╝")
    print("\n📍 RUTAS DISPONIBLES:")
    print("   🏠 Inicio: http://localhost:9090/")
    print("   📚 API Docs: http://localhost:9090/docs (Swagger interactivo)")
    print("   🔍 Health Check: http://localhost:9090/health")
    print("\n🔗 CONECTADOS A SERVICIOS JAVA:")
    print(f"   👤 User Service: {USER_SERVICE_URL}")
    print(f"   📦 Product Service: {PRODUCT_SERVICE_URL}")
    print(f"   🔍 Eureka Server: {EUREKA_SERVER}")
    print("\n💡 TIP: Abre http://localhost:9090/docs para probar los endpoints\n")


# ═════════════════════════════════════════════════════════════════════════════
# PUNTO DE ENTRADA
# ═════════════════════════════════════════════════════════════════════════════

if __name__ == "__main__":
    import uvicorn

    # Uvicorn es el servidor ASGI (equivalente a Tomcat en Java)
    # ASGI = Asynchronous Server Gateway Interface
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=REVIEW_SERVICE_PORT,
        log_level="info"
    )
