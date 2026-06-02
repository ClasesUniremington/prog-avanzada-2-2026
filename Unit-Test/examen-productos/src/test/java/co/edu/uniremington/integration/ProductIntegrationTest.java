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
        // TODO: ESTUDIANTE: 1) Crear producto
        // TODO: ESTUDIANTE: 2) Llamar al controlador
        // TODO: ESTUDIANTE: 3) Recuperar lista del repo
        // TODO: ESTUDIANTE: 4) Validar persistencia
    }
}
