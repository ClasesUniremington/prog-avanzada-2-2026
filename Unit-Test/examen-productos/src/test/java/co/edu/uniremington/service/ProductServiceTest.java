package co.edu.uniremington.service;

import co.edu.uniremington.model.Product;
import co.edu.uniremington.repository.ProductRepository;
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
        // TODO: ESTUDIANTE: 1) Instanciar un producto con datos válidos
            Product producto = new Product(1L, "Laptop", new BigDecimal("1200.00"));
        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
            productService.createProduct(producto);
        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
            verify(repository).save(productCaptor.capture());
        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
            Product productoCapturado = productCaptor.getValue();
            assertEquals(producto.getId(), productoCapturado.getId());
            assertEquals(producto.getName(), productoCapturado.getName());
            assertEquals(producto.getPrice(), productoCapturado.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
            Product producto = new Product(2L, "Mouse", new BigDecimal("-15.00"));

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(producto)
            );
        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException
        //                      al llamar a productService.createProduct()
            assertEquals("El precio no puede ser negativo", exception.getMessage());
    }
}
