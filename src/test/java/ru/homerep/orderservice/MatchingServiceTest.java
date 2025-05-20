package ru.homerep.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;
import ru.homerep.orderservice.config.HomeRepProperties;
import ru.homerep.orderservice.models.Address;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.models.dto.OrderRequest;
import ru.homerep.orderservice.services.LocationServiceClient;
import ru.homerep.orderservice.services.MatchingService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MatchingServiceTest {

    private static final double TEST_LAT = 55.75;
    private static final double TEST_LON = 37.61;
    private static final int RADIUS = 10;
    private static final String ORDER_TOPIC = "order-available-topic";

    private KafkaTemplate<String, OrderRequest> kafkaTemplate;
    private LocationServiceClient locationServiceClient;
    private MatchingService matchingService;
    private ObjectMapper objectMapper;
    private RestTemplate restTemplate;
    private HomeRepProperties homeRepProperties;

    @BeforeEach
    void setup() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        locationServiceClient = Mockito.mock(LocationServiceClient.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        homeRepProperties = new HomeRepProperties();
        homeRepProperties.setUserservice("http://82.202.143.3:8083");

        matchingService = new MatchingService(
                kafkaTemplate,
                locationServiceClient,
                objectMapper,
                restTemplate,
                homeRepProperties
        );
    }
    @Test
    void testFindWorker_whenWorkersAreFound() throws JsonProcessingException {
        Order order = createMockOrder(TEST_LAT, TEST_LON);
        long[] nearbyWorkers = new long[]{1L, 2L, 3L};

        when(locationServiceClient.getUsersByLatLng(TEST_LAT, TEST_LON, RADIUS))
                .thenReturn(nearbyWorkers);

        // Мокируем вызовы для получения email
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn("worker1@example.com")
                .thenReturn("worker2@example.com")
                .thenReturn("worker3@example.com")
                .thenReturn("customer@example.com");

        String orderJson = objectMapper.writeValueAsString(order);
        int result = matchingService.findWorker(orderJson);

        verify(locationServiceClient, times(1))
                .getUsersByLatLng(TEST_LAT, TEST_LON, RADIUS);

        // Проверяем, что было 4 вызова getForObject (3 worker + 1 customer)
        verify(restTemplate, times(4))
                .getForObject(anyString(), eq(String.class));

        verify(kafkaTemplate, times(nearbyWorkers.length))
                .send(eq(ORDER_TOPIC), any(OrderRequest.class));

        assertEquals(3, result);
    }

    @Test
    void testFindWorker_whenNoWorkersFound() throws JsonProcessingException {
        Order order = createMockOrder(TEST_LAT, TEST_LON);
        when(locationServiceClient.getUsersByLatLng(TEST_LAT, TEST_LON, RADIUS))
                .thenReturn(new long[]{});

        String orderJson = objectMapper.writeValueAsString(order);
        int result = matchingService.findWorker(orderJson);

        verify(locationServiceClient, times(1))
                .getUsersByLatLng(TEST_LAT, TEST_LON, RADIUS);

        verify(restTemplate, atLeastOnce())
                .getForObject(anyString(), eq(String.class));

        verify(kafkaTemplate, never())
                .send(anyString(), any(OrderRequest.class));

        assertEquals(0, result);
    }

    @Test
    void testFindWorker_whenInvalidJson() {
        String invalidJson = "{invalid}";
        assertThrows(JsonProcessingException.class, () -> {
            matchingService.findWorker(invalidJson);
        });
    }

    private Order createMockOrder(double lat, double lon) {
        Address address = new Address();
        address.setLatitude(lat);
        address.setLongitude(lon);

        Order order = new Order();
        order.setAddress(address);
        order.setId(123L);
        order.setCustomerId(100L);
        order.setCategory(new Category(1L, "Cleaning", "Cleaning services", 42L));
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }
}