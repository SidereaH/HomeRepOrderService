package ru.homerep.orderservice.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import ru.homerep.orderservice.models.Order;
//import com.homerep.models.Worker;
//import com.homerep.repository.WorkerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.homerep.orderservice.models.dto.OrderRequest;

import java.util.List;
@Slf4j
@Service
public class MatchingService {
//    private final WorkerRepository workerRepository;
    private final KafkaTemplate<String, OrderRequest> kafkaTemplate;

    private final LocationServiceClient locationServiceClient;
    private final ObjectMapper objectMapper;
    public MatchingService(
//            WorkerRepository workerRepository,
            KafkaTemplate<String, OrderRequest> kafkaTemplate,
            LocationServiceClient locationServiceClient, ObjectMapper objectMapper) {
//        this.workerRepository = workerRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.locationServiceClient = locationServiceClient;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "order-topic", groupId = "matching-service")
    public Integer findWorker(String orderJson) throws JsonProcessingException {
        Order order = objectMapper.readValue(orderJson, Order.class);
        Double lat = order.getAddress().getLatitude();
        Double lon = order.getAddress().getLongitude();
        //среди всех работников найти ближайших через микросервис userservice
        long[] nearbyWorkers = locationServiceClient.getUsersByLatLng(lat, lon,10 );

        if (nearbyWorkers.length > 0) {
            for (long worker : nearbyWorkers) {
                kafkaTemplate.send("order-available-topic", new OrderRequest(order.getId().toString(),order.getCategory().getName(), null, "hutornoyaa@gmail.com", null, order.getCreatedAt().toString(), null,"hutornoyaa@gmail.com",order.getCreatedAt().toString(),null));
                log.info("sended to notification-topic about avaliable topic");
            }
        } else {

            log.info("No available workers found for order: " + order.getId());
        }
        return nearbyWorkers.length;
    }
}
