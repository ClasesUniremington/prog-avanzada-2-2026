package co.edu.uniremington.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    Long userId,
    LocalDateTime createdAt,
    List<OrderItemDto> items
) {
}
