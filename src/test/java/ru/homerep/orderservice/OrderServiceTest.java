package ru.homerep.orderservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import ru.homerep.orderservice.models.*;
import ru.homerep.orderservice.repositories.*;
import ru.homerep.orderservice.services.OrderService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private KafkaTemplate<String, Order> kafkaTemplateOrder;
    private KafkaTemplate<String, String> kafkaTemplateNotification;
    private AddressRepository addressRepository;
    private CategoryRepository categoryRepository;
    private PaymentTypeRepository paymentTypeRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        kafkaTemplateOrder = mock(KafkaTemplate.class);
        kafkaTemplateNotification = mock(KafkaTemplate.class);
        addressRepository = mock(AddressRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        paymentTypeRepository = mock(PaymentTypeRepository.class);

        orderService = new OrderService(orderRepository, kafkaTemplateOrder, kafkaTemplateNotification,
                addressRepository, categoryRepository, paymentTypeRepository);
    }
    @Test
    void createOrder_success() {
        // Given
        Category category = new Category();
        category.setName("Cleaning");
        category.setId(1L);

        Address inputAddress = new Address();
        inputAddress.setStreetName("Main St");
        inputAddress.setBuildingNumber("10");
        inputAddress.setApartmentNumber("5");
        inputAddress.setCityName("Moscow");
        inputAddress.setLongitude(1.0);
        inputAddress.setLatitude(1.0);
        PaymentType paymentType = new PaymentType();
        paymentType.setName("MIR");
        paymentType.setId(1L);

        Order order = new Order();
        order.setCategory(category);
        order.setAddress(inputAddress);
        order.setPaymentType(paymentType);

        Address savedAddress = new Address();
        savedAddress.setId(1L);
        savedAddress.setStreetName(inputAddress.getStreetName());
        savedAddress.setBuildingNumber(inputAddress.getBuildingNumber());
        savedAddress.setApartmentNumber(inputAddress.getApartmentNumber());
        savedAddress.setCityName(inputAddress.getCityName());
        savedAddress.setLongitude(1.0);
        savedAddress.setLatitude(1.0);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCategory(category);
        savedOrder.setAddress(savedAddress);
        savedOrder.setPaymentType(paymentType);

        // When
        when(categoryRepository.findByName("Cleaning")).thenReturn(Optional.of(category));
        when(paymentTypeRepository.findByName("MIR")).thenReturn(Optional.of(paymentType));
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Then
        Optional<Order> result = orderService.createOrder(order);

        assertTrue(result.isPresent());
        assertEquals(savedOrder.getId(), result.get().getId());
        assertEquals(savedAddress.getId(), result.get().getAddress().getId());

        verify(addressRepository, times(1)).save(any(Address.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(kafkaTemplateOrder, times(1)).send(eq("order-topic"), any(Order.class));
    }
    @Test
    void createOrder_categoryNotFound() {
        Order order = new Order();
        Category category = new Category();
        category.setName("Repair");
        order.setCategory(category);

        when(categoryRepository.findByName("Repair")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
    }

    @Test
    void createOrder_paymentTypeNotFound() {
        Category category = new Category();
        category.setName("Repair");

        Address address = new Address();
        Order order = new Order();
        order.setCategory(category);
        order.setAddress(address);

        when(categoryRepository.findByName("Repair")).thenReturn(Optional.of(category));
        when(paymentTypeRepository.findByName("Repair")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
    }

    @Test
    void getAllOrders_returnsList() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void assignOrder_success() {
        Order order = new Order();
        order.setId(1L);
        order.setEmployeeId(null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.assignOrder(1L, 42L);

        assertEquals(42L, result.getEmployeeId());
        assertTrue(result.getAccepted());
        verify(orderRepository, times(1)).save(order);
        verify(kafkaTemplateNotification, times(1))
                .send(eq("notification-topic"), eq("Order 1 assigned to employee 42"));
    }

    @Test
    void assignOrder_notFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.assignOrder(999L, 1L));
    }

    @Test
    void assignOrder_alreadyAssigned() {
        Order order = new Order();
        order.setEmployeeId(5L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(RuntimeException.class, () -> orderService.assignOrder(1L, 42L));
    }
}
