package co.edu.uniremington.gateway_server;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
public class GatewayController {

    @Autowired
    private RestTemplate restTemplate;

    // User Service routes
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return restTemplate.getForEntity("http://localhost:8081/api/users", Object.class);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8081/api/users/" + id, Object.class);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Object user) {
        return restTemplate.postForEntity("http://localhost:8081/api/users", user, Object.class);
    }

    // Product Service routes
    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts() {
        return restTemplate.getForEntity("http://localhost:8082/api/products", Object.class);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8082/api/products/" + id, Object.class);
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@RequestBody Object product) {
        return restTemplate.postForEntity("http://localhost:8082/api/products", product, Object.class);
    }

    // Order Service routes
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        return restTemplate.getForEntity("http://localhost:8083/api/orders", Object.class);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return restTemplate.getForEntity("http://localhost:8083/api/orders/" + id, Object.class);
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Object order) {
        return restTemplate.postForEntity("http://localhost:8083/api/orders", order, Object.class);
    }
}
