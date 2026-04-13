package com.ecom.api.orderitem;

import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemCreateRequest;
import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemResponse;
import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemUpdateRequest;
import com.ecom.application.orderitem.OrderItemService;
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
 * T039 – REST controller for {@code /api/v1/order-items}.
 *
 * <p>Creating an item automatically:
 * <ul>
 *   <li>snapshots the current product price;</li>
 *   <li>decrements product stock;</li>
 *   <li>recalculates the parent order's {@code totalAmount}.</li>
 * </ul>
 */
@Tag(name = "Order Items", description = "Order line-item management – CRUD with stock and total management")
@RestController
@RequestMapping("/api/v1/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Operation(summary = "List all order items")
    @ApiResponse(responseCode = "200", description = "Order item list returned")
    @GetMapping
    public List<OrderItemResponse> findAll() {
        return orderItemService.findAll();
    }

    @Operation(summary = "Get order item by id")
    @ApiResponse(responseCode = "200", description = "Order item found")
    @ApiResponse(responseCode = "404", description = "Order item not found")
    @GetMapping("/{id}")
    public OrderItemResponse findById(@PathVariable String id) {
        return orderItemService.findById(id);
    }

    @Operation(summary = "Create order item",
               description = "Price is snapshotted from the product; stock is decremented; order totalAmount is recalculated.")
    @ApiResponse(responseCode = "201", description = "Order item created")
    @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Order or product not found")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse create(@Valid @RequestBody OrderItemCreateRequest req) {
        return orderItemService.create(req);
    }

    @Operation(summary = "Update order item quantity",
               description = "Stock is adjusted by the quantity delta; order totalAmount is recalculated.")
    @ApiResponse(responseCode = "200", description = "Order item updated")
    @ApiResponse(responseCode = "400", description = "Validation error or insufficient stock")
    @ApiResponse(responseCode = "404", description = "Order item not found")
    @PutMapping("/{id}")
    public OrderItemResponse update(@PathVariable String id,
                                    @Valid @RequestBody OrderItemUpdateRequest req) {
        return orderItemService.update(id, req);
    }

    @Operation(summary = "Delete order item",
               description = "Stock is restored; order totalAmount is recalculated.")
    @ApiResponse(responseCode = "204", description = "Order item deleted")
    @ApiResponse(responseCode = "404", description = "Order item not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        orderItemService.deleteById(id);
    }
}
