package com.app.service;

import com.app.model.Product;
import com.app.repository.ProductRepository;
import java.math.BigDecimal;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public void createProduct(Product p) {
        if (p.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        repository.save(p);
    }

    public java.util.List<Product> getAllProducts() {
        return repository.findAll();
    }
}
