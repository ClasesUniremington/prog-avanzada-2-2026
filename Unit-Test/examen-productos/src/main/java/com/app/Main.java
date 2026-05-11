package com.app;

import com.app.controller.ProductController;
import com.app.model.Product;
import com.app.repository.InMemoryProductRepository;
import com.app.repository.ProductRepository;
import com.app.service.ProductService;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        // Inyección de dependencias manual
        ProductRepository repository = new InMemoryProductRepository();
        ProductService service = new ProductService(repository);
        ProductController controller = new ProductController(service);

        // Prueba 1: Crear producto válido
        Product p1 = new Product(1L, "Laptop", new BigDecimal("1200.00"));
        System.out.println(controller.processCreateProduct(p1));

        // Prueba 2: Crear producto con precio negativo
        Product p2 = new Product(2L, "Mouse", new BigDecimal("-15.00"));
        System.out.println(controller.processCreateProduct(p2));

        // Mostrar productos finales
        System.out.println("\nLista de productos en el repositorio:");
        service.getAllProducts().forEach(System.out::println);
    }
}
