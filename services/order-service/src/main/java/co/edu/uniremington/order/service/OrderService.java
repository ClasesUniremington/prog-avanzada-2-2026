package co.edu.uniremington.order.service;

import co.edu.uniremington.order.dto.OrderRequest;
import co.edu.uniremington.order.dto.OrderResponse;
import co.edu.uniremington.order.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<OrderResponse> getAllOrders();

    Optional<OrderResponse> getOrderById(Long id);

    List<OrderResponse> getOrdersByUserId(Long userId);

    OrderResponse createOrder(OrderRequest orderRequest);

    void deleteOrder(Long id);

}
