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

    //Este test comprueba que, al enviar un producto válido al método createProduct(),
    // el servicio llama correctamente al repositorio para guardarlo y que el objeto enviado
    // conserva exactamente los mismos datos originales (id, nombre y precio).
    @Test
    public void debeGuardarProductoCorrectamente() {
        // 1) Instanciar un producto con datos válidos
        Product producto = new Product(1L, "Laptop", new BigDecimal("2500000"));

        //crea un producto
        //le asigna datos
        //llama al constructor
        //guarda el objeto en la variable producto

        // 2) Llamar al método createProduct del servicio
        productService.createProduct(producto);

        //ejecuta el método que quieres probar
        //activa la lógica del servicio
        //provoca la llamada al repositorio
        //permite verificar después si todo ocurrió correctamente

        // 3) Usar ArgumentCaptor para capturar lo que se envió al repositorio
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repository).save(captor.capture());

        //Crear una herramienta para capturar argumentos
        //Verificar que repository.save() fue llamado
        //Capturar el Product enviado al save()
        //Guardarlo dentro de captor

        // 4) Verificar que los datos capturados coinciden con los originales
        Product capturado = captor.getValue();
        assertEquals(1L, capturado.getId());
        assertEquals("Laptop", capturado.getName());
        assertEquals(new BigDecimal("2500000"), capturado.getPrice());

        //Obtener el Product capturado
        //Verificar el ID
        //Verificar el nombre
        //Verificar el precio
        //Confirmar que los datos no cambiaron
    }

    //Este test comprueba que, al intentar crear un producto con un precio negativo,
    //el método createProduct() valide correctamente los datos y lance una excepción
    // IllegalArgumentException para impedir que se guarde un producto inválido.
    @Test
    public void debeLanzarExcepcionCuandoElPrecioEsNegativo() {
        // 1) Instanciar un producto con precio negativo
        Product productoInvalido = new Product(2L, "Mouse", new BigDecimal("-10000"));

        //Crea un Product
        //Le asigna id = 2  name = Mouse   price = -10000
        //Guarda el objeto en productoInvalido
        //Se usa para probar validaciones

        // 2) Verificar que se lanza IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productoInvalido);
        });

        //Ejecutar createProduct(productoInvalido)
        //Esperar una IllegalArgumentException
        //Si ocurre → test exitoso
        //Si no ocurre → test falla
    }
}