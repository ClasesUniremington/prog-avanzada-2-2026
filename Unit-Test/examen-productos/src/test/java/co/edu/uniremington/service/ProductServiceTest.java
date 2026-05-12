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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void debeGuardarProductoCorrectamente() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con datos válidos
        Product product = new Product(3L, "Pantalla", new BigDecimal("1100000.00"));
        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
        productService.createProduct(product);
        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository, times(1)).save(captor.capture());
        Product captured = captor.getValue();
        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
        assertEquals(3L, captured.getId());
        assertEquals("Pantalla", captured.getName());
        assertEquals(new BigDecimal("1100000.00"), captured.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
        Product product = new Product(4L, "Tarjeta Grafica", new BigDecimal("-3000000.00"));
        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(product);
        });
        // 3. Assert: Verificar que nunca se llegó a guardar en el repositorio
        verify(repository, never()).save(any());
    }
}
