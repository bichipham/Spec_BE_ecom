package com.ecom.api.order;

import com.ecom.api.order.dto.OrderDtos.OrderCreateRequest;
import com.ecom.api.order.dto.OrderDtos.OrderResponse;
import com.ecom.api.order.dto.OrderDtos.OrderUpdateRequest;
import com.ecom.application.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * T038 – REST controller for {@code /api/v1/orders}.
 *
 * <p>PUT only accepts status updates; {@code totalAmount} is computed
 * automatically by {@code OrderItemService}.
 */
@Tag(name = "Orders", description = "Order management – CRUD and status transitions")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "List all orders")
    @ApiResponse(responseCode = "200", description = "Order list returned")
    @GetMapping
    public List<OrderResponse> findAll() {
        return orderService.findAll();
    }

    @Operation(summary = "Get order by id")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable String id) {
        return orderService.findById(id);
    }

    @Operation(summary = "Create order (starts in PENDING, totalAmount = 0)")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "User not found")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest req) {
        return orderService.create(req);
    }

    @Operation(summary = "Update order status",
               description = "Allowed transitions: PENDING→PAID, PAID→SHIPPED, PENDING→CANCELLED")
    @ApiResponse(responseCode = "200", description = "Order updated")
    @ApiResponse(responseCode = "400", description = "Invalid status transition")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable String id,
                                @Valid @RequestBody OrderUpdateRequest req) {
        return orderService.update(id, req);
    }

    @Operation(summary = "Delete order (must have no order items)")
    @ApiResponse(responseCode = "204", description = "Order deleted")
    @ApiResponse(responseCode = "400", description = "Order still has order items")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        orderService.deleteById(id);
    }
}
