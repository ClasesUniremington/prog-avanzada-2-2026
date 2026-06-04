# Microservices

## Puertos

- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8090`
- User service: `http://localhost:8081`
- Product service: `http://localhost:8082`
- Order service: `http://localhost:8083`

## Orden de ejecucion

1. `cd services\eureka-service && .\mvnw.cmd spring-boot:run`
2. `cd services\gateway-service && .\mvnw.cmd spring-boot:run`
3. `cd services\user-service && .\mvnw.cmd spring-boot:run`
4. `cd services\product-service && .\mvnw.cmd spring-boot:run`
5. `cd services\order-service && .\mvnw.cmd spring-boot:run`

## Pruebas por Gateway

- `http://localhost:8090/api/users/status`
- `http://localhost:8090/api/products/status`
- `http://localhost:8090/api/orders/status`

Cada servicio tambien se puede probar directo:

- `http://localhost:8081/users/status`
- `http://localhost:8082/products/status`
- `http://localhost:8083/orders/status`
