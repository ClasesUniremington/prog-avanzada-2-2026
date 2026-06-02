package co.edu.uniremington.controller;

import co.edu.uniremington.model.Product;
import co.edu.uniremington.service.ProductService;

public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    public String processCreateProduct(Product p) {
        try {
            service.createProduct(p);
            return "Producto creado con éxito: " + p.getName();
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error inesperado: " + e.getMessage();
        }
    }
}
