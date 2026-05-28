package co.edu.uniremington.order.service;

import co.edu.uniremington.order.dto.OrderItemDto;
import co.edu.uniremington.order.dto.OrderRequest;
import co.edu.uniremington.order.dto.OrderResponse;
import co.edu.uniremington.order.model.Order;
import co.edu.uniremington.order.model.OrderItem;
import co.edu.uniremington.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = new Order(orderRequest.userId());

        for (OrderItemDto itemDto : orderRequest.items()) {
            OrderItem item = new OrderItem(
                    itemDto.productId(),
                    itemDto.quantity(),
                    itemDto.price()
            );
            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        return convertToResponse(savedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderResponse convertToResponse(Order order) {
        List<OrderItemDto> items = order.getItems()
                .stream()
                .map(item -> new OrderItemDto(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getPrice()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                items
        );
    }

}
