package co.edu.uniremington.integration;

import co.edu.uniremington.controller.ProductController;
import co.edu.uniremington.model.Product;
import co.edu.uniremington.repository.InMemoryProductRepository;
import co.edu.uniremington.repository.ProductRepository;
import co.edu.uniremington.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductIntegrationTest {

    private ProductController controller;
    private ProductRepository repository;
    private ProductService service;

    @BeforeEach
    public void setUp() {
        repository = new InMemoryProductRepository();
        service = new ProductService(repository);
        controller = new ProductController(service);
    }

    @Test
    public void debeIntegrarCapasYCrearProductoExitosamente() {
        Product producto = new Product(1L, "Laptop", new BigDecimal("1200.00"));

        String respuesta = controller.processCreateProduct(producto);

        List<Product> productos = repository.findAll();

        assertEquals("Producto creado con éxito: Laptop", respuesta);
        assertEquals(1, productos.size());

        Product productoGuardado = productos.get(0);
        assertEquals(1L, productoGuardado.getId());
        assertEquals("Laptop", productoGuardado.getName());
        assertEquals(new BigDecimal("1200.00"), productoGuardado.getPrice());
    }

    @Test
    public void debeIntegrarCapasYRechazarProductoConPrecioNegativo() {
        Product producto = new Product(2L, "Mouse", new BigDecimal("-15.00"));

        String respuesta = controller.processCreateProduct(producto);

        List<Product> productos = repository.findAll();

        assertEquals("Error: El precio no puede ser negativo", respuesta);
        assertTrue(productos.isEmpty());
    }
}
