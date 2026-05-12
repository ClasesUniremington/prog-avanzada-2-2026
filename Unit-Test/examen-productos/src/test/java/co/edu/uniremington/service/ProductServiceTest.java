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
        Product product = new Product(1L, "Laptop", new BigDecimal("2500"));

        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
        productService.createProduct(product);

        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> captor =
                ArgumentCaptor.forClass(Product.class);
        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
        verify(repository).save(captor.capture());
        // 5. Obtener el producto capturado
        Product captured = captor.getValue();

        // 6. Validar que los datos sean iguales
        assertEquals(product.getId(), captured.getId());
        assertEquals(product.getName(), captured.getName());
        assertEquals(product.getPrice(), captured.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
        Product product = new Product(1L, "Laptop", new BigDecimal("-100")
        );
        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException 
        //   al llamar a productService.createProduct()

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product));
    }
}
