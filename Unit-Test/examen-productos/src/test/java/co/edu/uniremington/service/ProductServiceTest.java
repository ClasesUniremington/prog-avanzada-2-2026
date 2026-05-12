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
        Product producto = new Product();
        producto.setName("Laptop HP");
        producto.setPrice(new BigDecimal("1500.00"));

        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
        productService.createProduct(producto);

        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> productoCaptor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(productoCaptor.capture());

        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
        Product productoCaptured = productoCaptor.getValue();
        assertEquals("Laptop HP", productoCaptured.getName());
        assertEquals(new BigDecimal("1500.00"), productoCaptured.getPrice());

    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
        Product producto = new Product();
        producto.setName("Producto Inválido");
        producto.setPrice(new BigDecimal("-100.00"));

        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException 
        //                      al llamar a productService.createProduct()
        IllegalArgumentException exception = assertThrows( //assertThrows metodo que lanza una expresion especifica
                IllegalArgumentException.class,
                () -> productService.createProduct(producto)//ejecuta el metodo que se quiere probar
        );

    }
}
