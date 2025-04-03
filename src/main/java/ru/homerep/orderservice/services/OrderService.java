package ru.homerep.orderservice.services;


import lombok.extern.slf4j.Slf4j;
import ru.homerep.orderservice.models.Address;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.models.PaymentType;
import ru.homerep.orderservice.repositories.AddressRepository;
import ru.homerep.orderservice.repositories.CategoryRepository;
import ru.homerep.orderservice.repositories.OrderRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.homerep.orderservice.repositories.PaymentTypeRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Order> kafkaTemplateOrder;
    private final KafkaTemplate<String, String> kafkaTemplateNotification;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentTypeRepository paymentTypeRepository;

    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, String> kafkaTemplateNotification, AddressRepository addressRepository, CategoryRepository categoryRepository, PaymentTypeRepository paymentTypeRepository) {
        this.orderRepository = orderRepository;
        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateNotification = kafkaTemplateNotification;
        this.addressRepository = addressRepository;
        this.categoryRepository = categoryRepository;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public Optional<Order> createOrder(Order order) {
        Category category = order.getCategory();
        Category saved = categoryRepository.findByName(category.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        order.setCategory(saved);

        Address address = order.getAddress();
        addressRepository.save(address);

        PaymentType payment = paymentTypeRepository.findByName(category.getName()).orElseThrow(() -> new RuntimeException("Payment type not found"));
        order.setPaymentType(payment);
        Order savedOrder = orderRepository.save(order);
        kafkaTemplateOrder.send("order-topic", savedOrder); // Отправка в Kafka

        return Optional.of(savedOrder);
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

