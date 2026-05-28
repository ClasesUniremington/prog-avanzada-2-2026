package co.edu.uniremington.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
    @NotNull(message = "Product ID cannot be null")
    Long productId,

    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @Min(value = 0, message = "Price cannot be negative")
    Double price
) {
}
