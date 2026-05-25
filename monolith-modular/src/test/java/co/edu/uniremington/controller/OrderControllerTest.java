package co.edu.uniremington.controller;

import co.edu.uniremington.dto.OrderItemDto;
import co.edu.uniremington.dto.OrderRequest;
import co.edu.uniremington.model.Order;
import co.edu.uniremington.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order(1L);
        testOrder.setId(1L);
        testOrder.setCreatedAt(LocalDateTime.now());

        List<OrderItemDto> items = List.of(new OrderItemDto(1L, 2, 99.99));
        testOrderRequest = new OrderRequest(1L, items);
    }

    @Test
    void testGetAllOrders() throws Exception {
        List<Order> orders = List.of(testOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById_Found() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        List<Order> orders = List.of(testOrder);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1L));

        verify(orderService, times(1)).getOrdersByUserId(1L);
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void testCreateOrder_BadRequest() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid order"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void testDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

}
