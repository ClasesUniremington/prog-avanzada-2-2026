# Examen de Pruebas Unitarias e Integración - Lenguaje de Programación Avanzado 2
**Universidad Remington - Sede Manizales**  
**Facultad de Ingeniería de Sistemas**

**Docente:** Ing. Jose Castrillón

---

## 🎯 Objetivo del Examen
Evaluar la capacidad del estudiante para implementar pruebas unitarias (usando Mockito y JUnit 5) y pruebas de integración en un entorno Java 21 con Maven. El estudiante deberá completar la lógica de pruebas necesaria para asegurar la calidad de un sistema de gestión de productos.

## 🛠️ Tecnologías Requeridas
*   **Java:** Versión 21 o superior.
*   **Maven:** 3.8+ para la gestión de dependencias y ejecución de pruebas.
*   **JUnit 5 (Jupiter):** Framework de pruebas.
*   **Mockito:** Biblioteca para creación de Mocks.

## 📁 Estructura del Proyecto
El proyecto sigue una arquitectura MVC simulada:
*   `co.edu.uniremington.model`: Definición de la entidad `Product`.
*   `co.edu.uniremington.repository`: Interfaz y persistencia en memoria.
*   `co.edu.uniremington.service`: Lógica de negocio (validación de precios).
*   `co.edu.uniremington.controller`: Capa de entrada y manejo de excepciones.
*   `co.edu.uniremington.Main`: Ejemplo de ejecución manual.

---

## 📝 Tareas a Realizar

### 1. Pruebas Unitarias (`ProductServiceTest.java`)
Debe completar los métodos de prueba utilizando Mocks para el repositorio.
*   **`debeGuardarProductoCorrectamente`**:
    1.  Instanciar un producto válido.
    2.  Ejecutar la creación en el servicio.
    3.  Usar `ArgumentCaptor` para capturar el objeto enviado al repositorio.
    4.  Validar que los datos guardados sean idénticos a los enviados.
*   **`debeLanzarExcepcionCuandoElPrecioEsNegativo`**:
    1.  Crear un producto con precio menor a cero.
    2.  Utilizar `assertThrows` para verificar que el sistema lanza una `IllegalArgumentException`.

### 2. Pruebas de Integración (`ProductIntegrationTest.java`)
Debe completar el flujo de integración real (sin mocks).
*   **`debeIntegrarCapasYCrearProductoExitosamente`**:
    1.  Crear un objeto `Product`.
    2.  Llamar al método del controlador para procesar la creación.
    3.  Recuperar la lista de productos directamente desde el repositorio real.
    4.  Validar mediante aserciones que el producto existe en la lista y sus datos son correctos.

---

## 🚀 Cómo Ejecutar el Proyecto

### Opción 1: Usando la Terminal (Línea de Comandos)
1.  Abra una terminal en la carpeta raíz del proyecto `examen-productos`.
2.  Ejecute el comando para compilar y correr las pruebas:
    ```bash
    mvn test
    ```
3.  Para ejecutar la aplicación manualmente (Main):
    ```bash
    mvn exec:java -Dexec.mainClass="co.edu.uniremington.Main"
    ```

### Opción 2: Usando IntelliJ IDEA (Recomendado)
1.  Abra IntelliJ IDEA y seleccione **Open**.
2.  Navegue hasta la carpeta `examen-productos` y seleccione el archivo `pom.xml`. Seleccione **Open as Project**.
3.  Espere a que IntelliJ descargue las dependencias de Maven e indexe el proyecto.
4.  **Para ejecutar las pruebas:**
    *   Navegue en el panel de proyecto hasta la carpeta `src/test/java/...`.
    *   Puede hacer clic derecho sobre la clase de prueba (ej. `ProductServiceTest`) o sobre la carpeta `java` entera y seleccionar **Run 'Tests in...'** (o el ícono verde de play).
5.  **Para ejecutar la aplicación (Main):**
    *   Abra la clase `Main.java` en `src/main/java/...`.
    *   Haga clic en el ícono verde de "Play" (Run) ubicado junto a la firma del método `public static void main(String[] args)`.

## ⚠️ Criterios de Evaluación
*   Implementación correcta de Mockito (`@Mock`, `@InjectMocks`).
*   Uso adecuado de `ArgumentCaptor`.
*   Validación correcta de excepciones con `assertThrows`.
*   Uso de aserciones (`assertEquals`, `assertTrue`, etc.) para verificar resultados.
*   Cumplimiento de las buenas prácticas de nombrado y estructura de código.

---
*¡Mucho éxito en su examen!*
