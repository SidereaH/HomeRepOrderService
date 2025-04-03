package ru.homerep.orderservice.controllers;

import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.models.dto.AssignResponse;
import ru.homerep.orderservice.services.MatchingService;
import ru.homerep.orderservice.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final MatchingService matchingService;


    public OrderController(OrderService orderService, MatchingService matchingService) {
        this.orderService = orderService;
        this.matchingService =matchingService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @PostMapping("order/findWorker/")
    public ResponseEntity<Integer> findWorker(@RequestBody Order order) {
        return ResponseEntity.ok(matchingService.findWorker(order));
    }

    @PostMapping("order/{orderId}/assignWorker/{workerId}")
    public ResponseEntity<AssignResponse> assignWorker(@PathVariable String orderId, @PathVariable String workerId) {
        Order order = orderService.assignOrder(Long.parseLong(orderId), Long.parseLong(workerId));
        return ResponseEntity.ok(new AssignResponse("Запрос выполнен", order.getId(),order.getEmployeeId()));
    }
}
