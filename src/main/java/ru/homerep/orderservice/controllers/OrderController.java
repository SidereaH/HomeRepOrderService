package ru.homerep.orderservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.models.dto.AssignResponse;
import ru.homerep.orderservice.models.dto.DefaultResponse;
import ru.homerep.orderservice.repositories.OrderRepository;
import ru.homerep.orderservice.services.MatchingService;
import ru.homerep.orderservice.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final MatchingService matchingService;
    private final OrderRepository orderRepository;


    public OrderController(OrderService orderService, MatchingService matchingService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.matchingService =matchingService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<DefaultResponse<Order, String>> createOrder(@RequestBody Order order) {
        try {
            Optional<Order> savedOrder = orderService.createOrder(order);
            return ResponseEntity.ok(new DefaultResponse<>(savedOrder.get(), "Success"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new DefaultResponse<>(order, "Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @PostMapping("order/findWorker/")
    public ResponseEntity<Integer> findWorker(@RequestParam String orderID) {
        Order order = orderRepository.findById(Long.parseLong(orderID)).orElseThrow(() -> new RuntimeException("Order not found"));
        ObjectMapper mapper = new ObjectMapper();
        String orderJson = null;
        try {
            orderJson = mapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            return ResponseEntity.ok(matchingService.findWorker(orderJson));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("order/{orderId}/assignWorker/{workerId}")
    public ResponseEntity<AssignResponse> assignWorker(@PathVariable String orderId, @PathVariable String workerId) {
        Order order = orderService.assignOrder(Long.parseLong(orderId), Long.parseLong(workerId));
        return ResponseEntity.ok(new AssignResponse("Запрос выполнен", order.getId(),order.getEmployeeId()));
    }
//    @DeleteMapping()
//    public ResponseEntity<DefaultResponse<PaymentType,String>> deletePayment(@RequestParam String paymentTypeName) {
//        try {
//            PaymentType toDel = orderservice.findByName(paymentTypeName).orElseThrow(() -> new RuntimeException("Payment type does not exist"));
//            paymentTypeRepository.delete(toDel);
//            return ResponseEntity.ok(new DefaultResponse<>(toDel, "Success"));
//        } catch (RuntimeException e){
//            return ResponseEntity.badRequest()
//                    .body(new DefaultResponse<>(null, "Error: " + e.getMessage()));
//        }
//    }
}
