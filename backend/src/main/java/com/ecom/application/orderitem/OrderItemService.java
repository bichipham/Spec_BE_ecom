package com.ecom.application.orderitem;

import com.ecom.api.common.ResourceNotFoundException;
import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemCreateRequest;
import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemResponse;
import com.ecom.api.orderitem.dto.OrderItemDtos.OrderItemUpdateRequest;
import com.ecom.domain.model.Order;
import com.ecom.domain.model.OrderItem;
import com.ecom.domain.model.Product;
import com.ecom.domain.repository.OrderItemRepository;
import com.ecom.domain.repository.OrderRepository;
import com.ecom.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * T035 – Application service for OrderItem CRUD with stock management and total recalculation.
 *
 * <p>Business rules:
 * <ul>
 *   <li>{@code orderId} and {@code productId} must reference existing entities.</li>
 *   <li>{@code quantity} must not exceed the product's current stock.</li>
 *   <li>{@code price} is a <em>snapshot</em> of the product price at creation time;
 *       it never changes when the product price changes later.</li>
 *   <li>Creating, updating, or deleting an item triggers {@link Order#getTotalAmount()}
 *       recalculation on the parent order.</li>
 *   <li>Product stock is decremented on create/increase, restored on delete/decrease.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemResponse create(OrderItemCreateRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order", req.getOrderId()));

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> ResourceNotFoundException.of("Product", req.getProductId()));

        if (product.getStock() < req.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product id='" + req.getProductId()
                    + "': available=" + product.getStock() + ", requested=" + req.getQuantity());
        }

        Instant now = Instant.now();
        OrderItem item = OrderItem.builder()
                .id(UUID.randomUUID().toString())
                .orderId(order.getId())
                .productId(product.getId())
                .quantity(req.getQuantity())
                .price(product.getPrice())   // snapshot – not taken from request
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Decrement stock
        product.setStock(product.getStock() - req.getQuantity());
        product.setUpdatedAt(now);
        productRepository.save(product);

        orderItemRepository.save(item);
        recalculateTotalAmount(order.getId());

        log.info("Created order-item id={} for order={}", item.getId(), order.getId());
        return OrderItemResponse.from(item);
    }

    public OrderItemResponse findById(String id) {
        return OrderItemResponse.from(
                orderItemRepository.findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("OrderItem", id))
        );
    }

    public List<OrderItemResponse> findAll() {
        return orderItemRepository.findAll().stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());
    }

    public OrderItemResponse update(String id, OrderItemUpdateRequest req) {
        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderItem", id));

        Product product = productRepository.findById(existing.getProductId())
                .orElseThrow(() -> ResourceNotFoundException.of("Product", existing.getProductId()));

        int delta = req.getQuantity() - existing.getQuantity();
        if (delta > 0 && product.getStock() < delta) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product id='" + existing.getProductId()
                    + "': available=" + product.getStock() + ", additional requested=" + delta);
        }

        // Adjust stock by the net delta (negative delta restores stock)
        product.setStock(product.getStock() - delta);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);

        existing.setQuantity(req.getQuantity());
        existing.setUpdatedAt(Instant.now());
        orderItemRepository.save(existing);

        recalculateTotalAmount(existing.getOrderId());

        log.info("Updated order-item id={} quantity={}", id, req.getQuantity());
        return OrderItemResponse.from(existing);
    }

    public void deleteById(String id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderItem", id));

        // Restore product stock before removing the item
        productRepository.findById(item.getProductId()).ifPresent(product -> {
            product.setStock(product.getStock() + item.getQuantity());
            product.setUpdatedAt(Instant.now());
            productRepository.save(product);
        });

        String orderId = item.getOrderId();
        orderItemRepository.deleteById(id);
        recalculateTotalAmount(orderId);

        log.info("Deleted order-item id={}", id);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Recomputes {@code Order.totalAmount} as the sum of (price × quantity)
     * for all remaining items on the order.
     */
    private void recalculateTotalAmount(String orderId) {
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orderRepository.findById(orderId).ifPresent(order -> {
            order.setTotalAmount(total);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
        });
    }
}
