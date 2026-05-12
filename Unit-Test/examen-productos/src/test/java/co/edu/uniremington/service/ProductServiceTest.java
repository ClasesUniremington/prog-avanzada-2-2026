package co.edu.uniremington.service;

import co.edu.uniremington.model.Product;
import co.edu.uniremington.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void debeGuardarProductoCorrectamente() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con datos válidos

        Product product = new Product(1L, "Laptop", new BigDecimal("1500.00"));

        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService

        productService.createProduct(product);
        Mockito.verify(repository, Mockito.times(1)).save(product);


        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(repository).save(captor.capture());
        Product capturado = captor.getValue();

        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados

        assertEquals("Laptop", capturado.getName());
        assertEquals(new BigDecimal("1500.00"), capturado.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo

        Product product = new Product(2L, "Mouse", new BigDecimal("-10.00"));

        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException
        //                      al llamar a productService.createProduct()
        assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product)
        );

    }
}
