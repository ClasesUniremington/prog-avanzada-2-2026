package co.edu.uniremington.repository;

import co.edu.uniremington.model.Product;
import java.util.List;

public interface ProductRepository {
    void save(Product p);
    List<Product> findAll();
}
