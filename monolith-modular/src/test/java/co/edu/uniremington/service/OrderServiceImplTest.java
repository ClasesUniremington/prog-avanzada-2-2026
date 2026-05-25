package co.edu.uniremington.service;

import co.edu.uniremington.dto.OrderItemDto;
import co.edu.uniremington.dto.OrderRequest;
import co.edu.uniremington.model.Order;
import co.edu.uniremington.repository.OrderRepository;
import co.edu.uniremington.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order(1L);
        testOrder.setId(1L);

        List<OrderItemDto> items = List.of(
                new OrderItemDto(1L, 2, 99.99)
        );
        testOrderRequest = new OrderRequest(1L, items);
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrderById_Found() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(999L);

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void testGetOrdersByUserId() {
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByUserId(1L)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testCreateOrder_Success() {
        when(userService.userExists(1L)).thenReturn(true);
        when(productService.productExists(1L)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        Order result = orderService.createOrder(testOrderRequest);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(userService, times(1)).userExists(1L);
        verify(productService, times(1)).productExists(1L);
        verify(productService, times(1)).decreaseStock(1L, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCreateOrder_UserNotFound() {
        when(userService.userExists(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(testOrderRequest));
        verify(userService, times(1)).userExists(1L);
        verify(productService, never()).productExists(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCreateOrder_ProductNotFound() {
        when(userService.userExists(1L)).thenReturn(true);
        when(productService.productExists(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(testOrderRequest));
        verify(userService, times(1)).userExists(1L);
        verify(productService, times(1)).productExists(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCreateOrder_InsufficientStock() {
        when(userService.userExists(1L)).thenReturn(true);
        when(productService.productExists(1L)).thenReturn(true);
        doThrow(new IllegalArgumentException("Insufficient stock"))
                .when(productService).decreaseStock(1L, 2);

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(testOrderRequest));
        verify(userService, times(1)).userExists(1L);
        verify(productService, times(1)).productExists(1L);
        verify(productService, times(1)).decreaseStock(1L, 2);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testDeleteOrder() {
        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

}
