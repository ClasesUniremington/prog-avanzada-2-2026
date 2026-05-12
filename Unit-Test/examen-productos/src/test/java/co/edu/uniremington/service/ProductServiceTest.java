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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository repository;
    @InjectMocks
    private ProductService productService;

    @Test
    public void debeGuardarProductoCorrectamente() {
        // Descripción: esta prueba verifica que el servicio guarde correctamente
        // un producto válido enviándolo al repositorio con los mismos datos recibidos.
        // TODO: ESTUDIANTE: 1) Instanciar un producto con datos válidos
        Product producto = new Product(1L, "Laptop", new BigDecimal("1200.00"));
        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
        productService.createProduct(producto);
        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
        Product productoCapturado = captor.getValue();
        assertEquals(producto.getId(), productoCapturado.getId());
        assertEquals(producto.getName(), productoCapturado.getName());
        assertEquals(producto.getPrice(), productoCapturado.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // Descripción: esta prueba verifica que el servicio rechace un producto
        // con precio negativo y lance una excepción sin guardarlo en el repositorio.
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
        Product producto = new Product(2L, "Mouse", new BigDecimal("-15.00"));
        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException
        //                      al llamar a productService.createProduct()
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(producto)
        );
        assertEquals("El precio no puede ser negativo", exception.getMessage());
        verify(repository, never()).save(producto);
    }
}
