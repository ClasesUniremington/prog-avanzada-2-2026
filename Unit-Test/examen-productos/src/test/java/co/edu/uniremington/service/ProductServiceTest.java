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
        Product product = new Product();
        product.setName("play 5");
        product.setPrice(new  BigDecimal("500.00"));
        // TODO: ESTUDIANTE: 2) Llamar al método createProduct del productService
        productService.createProduct(product);
        // TODO: ESTUDIANTE: 3) Usar ArgumentCaptor para capturar el producto enviado al repositorio
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());
        Product saverProduct = captor.getValue();//extraemos el producto o objeto que es capturado
        // TODO: ESTUDIANTE: 4) Verificar con assert que los datos del producto capturado coinciden con los creados
        assertEquals(product.getName(), saverProduct.getName());
        assertEquals(product.getPrice(), saverProduct.getPrice());
    }

    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // TODO: ESTUDIANTE: 1) Instanciar un producto con precio negativo
        Product productNeg = new Product();
        productNeg.setName("play 5");
        productNeg.setPrice(new  BigDecimal("-500.00"));
        // TODO: ESTUDIANTE: 2) Usar assertThrows para verificar que se lanza IllegalArgumentException 
        //                      al llamar a productService.createProduct()
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productNeg));
    }
}
