package co.edu.uniremington.service;

import co.edu.uniremington.dto.OrderRequest;
import co.edu.uniremington.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    List<Order> getAllOrders();

    Optional<Order> getOrderById(Long id);

    List<Order> getOrdersByUserId(Long userId);

    Order createOrder(OrderRequest orderRequest);

    void deleteOrder(Long id);

}
