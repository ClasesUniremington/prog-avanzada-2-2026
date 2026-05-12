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
        // 1) Instanciar un producto con datos válidos
        Product product = new Product(1L, "Celular", new BigDecimal("1200.00"));

        // 2) Llamar al método createProduct del productService
        productService.createProduct(product);

        // 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(repository).save(captor.capture());

        // Obtenemos el valor que el capturador
        Product productoCapturado = captor.getValue();

        // 4)Verificar con assert que los datos del producto capturado coinciden con los creados
        assertEquals("Celular", productoCapturado.getName());
        assertEquals(new BigDecimal("1200.00"), productoCapturado.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // 1) Instanciar un producto con precio negativo
        Product productInvalido = new Product(2L, "Error", new BigDecimal("-50.00"));

        // 2) Usar assertThrows para verificar que se lanza IllegalArgumentException
        //    al llamar a productService.createProduct()
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productInvalido);
        });
    }
}
