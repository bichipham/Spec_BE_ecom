package com.ecom.application.order;

import com.ecom.api.common.ResourceNotFoundException;
import com.ecom.api.order.dto.OrderDtos.OrderCreateRequest;
import com.ecom.api.order.dto.OrderDtos.OrderResponse;
import com.ecom.api.order.dto.OrderDtos.OrderUpdateRequest;
import com.ecom.domain.model.Order;
import com.ecom.domain.model.Order.OrderStatus;
import com.ecom.domain.repository.OrderItemRepository;
import com.ecom.domain.repository.OrderRepository;
import com.ecom.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * T034 – Application service for Order CRUD and status transitions.
 *
 * <p>Business rules:
 * <ul>
 *   <li>The {@code userId} must reference an existing user.</li>
 *   <li>New orders always start in {@link OrderStatus#PENDING} with {@code totalAmount = 0}.</li>
 *   <li>Only these status transitions are permitted:
 *       <pre>PENDING → PAID → SHIPPED</pre>
 *       <pre>PENDING → CANCELLED</pre>
 *   </li>
 *   <li>An order with existing order items cannot be deleted; remove items first.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderResponse create(OrderCreateRequest req) {
        if (!userRepository.existsById(req.getUserId())) {
            throw ResourceNotFoundException.of("User", req.getUserId());
        }

        Instant now = Instant.now();
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .userId(req.getUserId())
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        log.info("Creating order for userId={}", req.getUserId());
        return OrderResponse.from(orderRepository.save(order));
    }

    public OrderResponse findById(String id) {
        return OrderResponse.from(
                orderRepository.findById(id)
                        .orElseThrow(() -> ResourceNotFoundException.of("Order", id))
        );
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    public OrderResponse update(String id, OrderUpdateRequest req) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", id));

        OrderStatus previousStatus = existing.getStatus();
        validateTransition(previousStatus, req.getStatus());
        existing.setStatus(req.getStatus());
        existing.setUpdatedAt(Instant.now());

        log.info("Order id={} status {} → {}", id, previousStatus, req.getStatus());
        return OrderResponse.from(orderRepository.save(existing));
    }

    public void deleteById(String id) {
        if (!orderRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Order", id);
        }
        if (!orderItemRepository.findByOrderId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot delete order id='" + id + "': remove its order items first");
        }
        log.info("Deleting order id={}", id);
        orderRepository.deleteById(id);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Enforces the allowed state-machine transitions.
     *
     * <pre>
     *   PENDING → PAID | CANCELLED
     *   PAID    → SHIPPED
     *   SHIPPED → (terminal – no transitions)
     *   CANCELLED → (terminal – no transitions)
     * </pre>
     */
    private void validateTransition(OrderStatus from, OrderStatus to) {
        boolean valid = switch (from) {
            case PENDING -> to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
            case PAID -> to == OrderStatus.SHIPPED;
            case SHIPPED, CANCELLED -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + from + " → " + to);
        }
    }
}
