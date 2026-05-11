package com.app.repository;

import com.app.model.Product;
import java.util.ArrayList;
import java.util.List;

public class InMemoryProductRepository implements ProductRepository {
    private final List<Product> products = new ArrayList<>();

    @Override
    public void save(Product p) {
        products.add(p);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products);
    }
}
