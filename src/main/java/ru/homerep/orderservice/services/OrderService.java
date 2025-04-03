package ru.homerep.orderservice.services;


import lombok.extern.slf4j.Slf4j;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.repositories.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Order> kafkaTemplateOrder;
    private final KafkaTemplate<String, String> kafkaTemplateNotification;
    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, String> kafkaTemplateNotification) {
        this.orderRepository = orderRepository;
        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateNotification = kafkaTemplateNotification;
    }

    public Order createOrder(Order order) {
        Order savedOrder = orderRepository.save(order);
        kafkaTemplateOrder.send("order-topic", savedOrder); // Отправка в Kafka
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order assignOrder(Long orderId, Long employeeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getEmployeeId() != null) {
            throw new RuntimeException("Order already assigned");
        }

        order.setEmployeeId(employeeId);
        order.setAccepted(true);
        orderRepository.save(order);

        kafkaTemplateNotification.send("notification-topic", "Order " + orderId + " assigned to employee " + employeeId);
        log.info("Order {} assigned to employee {}", orderId, employeeId);
        return order;
    }
}

