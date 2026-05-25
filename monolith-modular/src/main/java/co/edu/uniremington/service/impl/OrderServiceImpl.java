package co.edu.uniremington.service.impl;

import co.edu.uniremington.dto.OrderRequest;
import co.edu.uniremington.model.Order;
import co.edu.uniremington.model.OrderItem;
import co.edu.uniremington.repository.OrderRepository;
import co.edu.uniremington.service.OrderService;
import co.edu.uniremington.service.ProductService;
import co.edu.uniremington.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final UserService userService;

    public OrderServiceImpl(OrderRepository orderRepository,
                          ProductService productService,
                          UserService userService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order createOrder(OrderRequest orderRequest) {
        if (!userService.userExists(orderRequest.userId())) {
            throw new IllegalArgumentException("User not found with id: " + orderRequest.userId());
        }

        Order order = new Order(orderRequest.userId());

        orderRequest.items().forEach(itemDto -> {
            if (!productService.productExists(itemDto.productId())) {
                throw new IllegalArgumentException("Product not found with id: " + itemDto.productId());
            }

            productService.decreaseStock(itemDto.productId(), itemDto.quantity());

            OrderItem orderItem = new OrderItem(itemDto.productId(), itemDto.quantity(), itemDto.price());
            order.addItem(orderItem);
        });

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

}
