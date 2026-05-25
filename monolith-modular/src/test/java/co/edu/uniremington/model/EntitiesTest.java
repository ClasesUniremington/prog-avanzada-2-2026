package co.edu.uniremington.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntitiesTest {

    @Test
    void testProduct() {
        Product p = new Product("Laptop", "Gaming", 999.99, 10);
        p.setId(1L);
        p.setName("Desktop");
        p.setDescription("Workstation");
        p.setPrice(1499.99);
        p.setStock(5);

        assertEquals(1L, p.getId());
        assertEquals("Desktop", p.getName());
        assertEquals("Workstation", p.getDescription());
        assertEquals(1499.99, p.getPrice());
        assertEquals(5, p.getStock());
    }

    @Test
    void testUser() {
        User u = new User("John", "john@test.com", "555-1234", "123 Main");
        u.setId(1L);
        u.setName("Jane");
        u.setEmail("jane@test.com");
        u.setPhone("555-5678");
        u.setAddress("456 Oak");

        assertEquals(1L, u.getId());
        assertEquals("Jane", u.getName());
        assertEquals("jane@test.com", u.getEmail());
        assertEquals("555-5678", u.getPhone());
        assertEquals("456 Oak", u.getAddress());
    }

    @Test
    void testOrder() {
        Order o = new Order(1L);
        o.setId(1L);
        o.setUserId(2L);
        LocalDateTime now = LocalDateTime.now();
        o.setCreatedAt(now);

        assertEquals(1L, o.getId());
        assertEquals(2L, o.getUserId());
        assertEquals(now, o.getCreatedAt());
        assertTrue(o.getItems().isEmpty());
    }

    @Test
    void testOrderItem() {
        OrderItem oi = new OrderItem(1L, 2, 99.99);
        oi.setId(1L);
        oi.setProductId(2L);
        oi.setQuantity(5);
        oi.setPrice(149.99);

        assertEquals(1L, oi.getId());
        assertEquals(2L, oi.getProductId());
        assertEquals(5, oi.getQuantity());
        assertEquals(149.99, oi.getPrice());
    }

    @Test
    void testOrderAddRemoveItems() {
        Order o = new Order(1L);
        OrderItem oi = new OrderItem(1L, 2, 99.99);

        o.addItem(oi);
        assertEquals(1, o.getItems().size());
        assertEquals(o, oi.getOrder());

        o.removeItem(oi);
        assertEquals(0, o.getItems().size());
        assertNull(oi.getOrder());
    }

}
