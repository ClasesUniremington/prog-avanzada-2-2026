package com.app.repository;

import com.app.model.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product p);
    List<Product> findAll();
}
