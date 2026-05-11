package com.app.controller;

import com.app.model.Product;
import com.app.service.ProductService;

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
