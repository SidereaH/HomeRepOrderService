package ru.homerep.orderservice;



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

    @BeforeEach
    void setup() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        locationServiceClient = Mockito.mock(LocationServiceClient.class);
        matchingService = new MatchingService(kafkaTemplate, locationServiceClient);
    }

    @Test
    void testFindWorker_whenWorkersAreFound() {
        Order order = createMockOrder(55.75, 37.61);
        long[] nearbyWorkers = new long[]{1L, 2L, 3L};

        when(locationServiceClient.getUsersByLatLng(55.75, 37.61, 10)).thenReturn(nearbyWorkers);

        int result = matchingService.findWorker(order);

        verify(locationServiceClient, times(1)).getUsersByLatLng(55.75, 37.61, 10);
        verify(kafkaTemplate, times(3)).send(eq("notification-topic"), contains("New order available for worker"));
        assert result == 3;
    }

    @Test
    void testFindWorker_whenNoWorkersFound() {
        Order order = createMockOrder(55.75, 37.61);
        when(locationServiceClient.getUsersByLatLng(55.75, 37.61, 10)).thenReturn(new long[]{});

        int result = matchingService.findWorker(order);

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

