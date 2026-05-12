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
       // 1) Instanciar un producto con datos válidos
       Product producto = new Product(1L, "Teclado Mecánico", new BigDecimal("350000.0"));

    // 2) Llamar al método createProduct del productService
       productService.createProduct(producto);

    // 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
       ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
       verify(repository).save(captor.capture());
       Product productoCaptado = captor.getValue();
   
    // 4) Verificar que los datos coinciden
       assertEquals(producto.getId(),    productoCaptado.getId());
       assertEquals(producto.getName(),  productoCaptado.getName());
       assertEquals(producto.getPrice(), productoCaptado.getPrice());
   }


    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // 1) Instanciar un producto con precio negativo
        Product productoInvalido = new Product(2L, "Producto Malo", new BigDecimal("-100.0"));

        // 2) Verificar que se lanza IllegalArgumentException
        assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(productoInvalido)
        );
    }
}