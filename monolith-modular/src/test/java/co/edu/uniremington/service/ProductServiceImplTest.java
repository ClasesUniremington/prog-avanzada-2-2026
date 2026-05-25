package co.edu.uniremington.service;

import co.edu.uniremington.model.Product;
import co.edu.uniremington.repository.ProductRepository;
import co.edu.uniremington.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product("Test Product", "Test Description", 99.99, 100);
        testProduct.setId(1L);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = List.of(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(999L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testUpdateProduct_Success() {
        Product updatedProduct = new Product("Updated Product", "Updated Description", 149.99, 50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        Product updatedProduct = new Product("Updated Product", "Updated Description", 149.99, 50);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(999L, updatedProduct));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testProductExists_True() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.productExists(1L);

        assertTrue(result);
        verify(productRepository, times(1)).existsById(1L);
    }

    @Test
    void testProductExists_False() {
        when(productRepository.existsById(999L)).thenReturn(false);

        boolean result = productService.productExists(999L);

        assertFalse(result);
        verify(productRepository, times(1)).existsById(999L);
    }

    @Test
    void testDecreaseStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.decreaseStock(1L, 10);

        assertEquals(90, testProduct.getStock());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testDecreaseStock_InsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(IllegalArgumentException.class, () -> productService.decreaseStock(1L, 200));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDecreaseStock_ProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.decreaseStock(999L, 10));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any());
    }

}
