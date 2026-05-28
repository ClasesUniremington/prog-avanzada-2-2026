package co.edu.uniremington.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrderRequest(
    @NotNull(message = "User ID cannot be null")
    Long userId,

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    List<OrderItemDto> items
) {
}
