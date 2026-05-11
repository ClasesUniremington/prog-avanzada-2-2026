package com.app.service;

import com.app.model.Product;
import com.app.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void debeGuardarProductoCorrectamente() {
        Product product = new Product(1L, "Teclado", new BigDecimal("50.00"));
        
        productService.createProduct(product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        
        Product savedProduct = captor.getValue();
        assertEquals("Teclado", savedProduct.getName());
        assertEquals(new BigDecimal("50.00"), savedProduct.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        Product product = new Product(1L, "Monitor", new BigDecimal("-100.00"));
        
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(product);
        });
    }
}
