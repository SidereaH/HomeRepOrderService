package ru.homerep.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import ru.homerep.orderservice.models.Address;
import ru.homerep.orderservice.models.Order;
import ru.homerep.orderservice.services.LocationServiceClient;
import ru.homerep.orderservice.services.MatchingService;

import static org.mockito.Mockito.*;

class MatchingServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private LocationServiceClient locationServiceClient;
    private MatchingService matchingService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        locationServiceClient = Mockito.mock(LocationServiceClient.class);

        // Создаем реальный ObjectMapper с конфигурацией для тестов
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        matchingService = new MatchingService(kafkaTemplate, locationServiceClient, objectMapper);
    }

    @Test
    void testFindWorker_whenWorkersAreFound() throws JsonProcessingException {
        Order order = createMockOrder(55.75, 37.61);
        long[] nearbyWorkers = new long[]{1L, 2L, 3L};

        when(locationServiceClient.getUsersByLatLng(55.75, 37.61, 10)).thenReturn(nearbyWorkers);

        String orderJson = objectMapper.writeValueAsString(order);
        int result = matchingService.findWorker(orderJson);

        verify(locationServiceClient, times(1)).getUsersByLatLng(55.75, 37.61, 10);
        verify(kafkaTemplate, times(3)).send(eq("notification-topic"), contains("New order available for worker"));
        assert result == 3;
    }

    @Test
    void testFindWorker_whenNoWorkersFound() throws JsonProcessingException {
        Order order = createMockOrder(55.75, 37.61);
        when(locationServiceClient.getUsersByLatLng(55.75, 37.61, 10)).thenReturn(new long[]{});

        String orderJson = objectMapper.writeValueAsString(order);
        int result = matchingService.findWorker(orderJson);

        verify(locationServiceClient, times(1)).getUsersByLatLng(55.75, 37.61, 10);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
        assert result == 0;
    }

    private Order createMockOrder(double lat, double lon) {
        Address address = new Address();
        address.setLatitude(lat);
        address.setLongitude(lon);

        Order order = new Order();
        order.setAddress(address);
        order.setId(123L);

        return order;
    }
}