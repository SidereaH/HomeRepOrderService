package ru.homerep.orderservice.services;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ru.homerep.orderservice.config.HomeRepProperties;
import ru.homerep.orderservice.models.Address;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.models.PaymentType;
import ru.homerep.orderservice.models.dto.OrderRequest;
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
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Order> kafkaTemplateOrder;
    private final KafkaTemplate<String, OrderRequest> kafkaTemplateNotification;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepository;
    private final PaymentTypeRepository paymentTypeRepository;
    private final RestTemplate restTemplate;

    private final String USER_SERVICE_URL;
    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, OrderRequest> kafkaTemplateNotification, AddressRepository addressRepository, CategoryRepository categoryRepository, PaymentTypeRepository paymentTypeRepository, RestTemplate restTemplate, HomeRepProperties props) {
        this.orderRepository = orderRepository;
        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateNotification = kafkaTemplateNotification;
        this.addressRepository = addressRepository;
        this.categoryRepository = categoryRepository;
        this.paymentTypeRepository = paymentTypeRepository;
        this.restTemplate = restTemplate;
        USER_SERVICE_URL = props.getUserservice() + "/clients";
    }

    public Optional<Order> createOrder(Order order) {
        Category category = order.getCategory();
        Category saved = categoryRepository.findByName(category.getName()).orElseThrow(() -> new RuntimeException("Category not found"));
        order.setCategory(saved);

        Address address;
        if(order.getAddress().getLatitude() == 0 ||order.getAddress().getLatitude().equals(null)  ) {
            address = new Address(order.getAddress().getStreetName(), order.getAddress().getBuildingNumber(), order.getAddress().getApartmentNumber(), order.getAddress().getCityName());
        }
        else{
            address = order.getAddress();
        }
        addressRepository.save(address);

        PaymentType payment = paymentTypeRepository.findByName(order.getPaymentType().getName()).orElseThrow(() -> new RuntimeException("Payment type not found"));
        order.setPaymentType(payment);
        order.setAddress(address);
        Order savedOrder = orderRepository.save(order);
        kafkaTemplateOrder.send("order-topic", savedOrder);

        return Optional.of(savedOrder);
    }
    @Transactional
    public Order updateOrder(Order order) {
        return orderRepository.findById(order.getId())
                .map(orderToUpdate -> {
                    orderToUpdate.setAddress(order.getAddress());
                    orderToUpdate.setCategory(order.getCategory());
                    orderToUpdate.setPaymentType(order.getPaymentType());
                    orderToUpdate.setEmployeeId(order.getEmployeeId());
//                    orderToUpdate.setAccepted(order.isAccepted());
                    orderToUpdate.setDescription(order.getDescription());
                    return orderRepository.save(orderToUpdate);
                }).orElseThrow(() -> new RuntimeException("Order not found"));
    }
    @Transactional
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order getOrderById(Long id){
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
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
        getUserEmail(order.getCustomerId());

        kafkaTemplateNotification.send("master-found-topic", new OrderRequest(order.getId().toString(),order.getCategory().getName(), null, getUserEmail(order.getCustomerId()), null, order.getCreatedAt().toString(), null,getUserEmail(employeeId),order.getCreatedAt().toString(),null));
        log.info("Order {} assigned to employee {}", orderId, employeeId);
        return order;
    }
    private String getUserEmail(Long userId) {
        if (userId != null)   {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId + "/mail", String.class);

        }
        else return "hutornoyaa@gmail.com";
    }
    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findOrdersByCustomerId(clientId);
    }

}

