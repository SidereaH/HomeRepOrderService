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

import java.util.List;
@Slf4j
@Service
public class MatchingService {
//    private final WorkerRepository workerRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final LocationServiceClient locationServiceClient;
    private final ObjectMapper objectMapper;
    public MatchingService(
//            WorkerRepository workerRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            LocationServiceClient locationServiceClient, ObjectMapper objectMapper) {
//        this.workerRepository = workerRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.locationServiceClient = locationServiceClient;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "order-topic", groupId = "matching-group")
    public Integer findWorker(String orderJson) throws JsonProcessingException {
        Order order = objectMapper.readValue(orderJson, Order.class);
        Double lat = order.getAddress().getLatitude();
        Double lon = order.getAddress().getLongitude();
        //среди всех работников найти ближайших через микросервис userservice
        long[] nearbyWorkers = locationServiceClient.getUsersByLatLng(lat, lon,10 );

        if (nearbyWorkers.length > 0) {
            for (long worker : nearbyWorkers) {
                kafkaTemplate.send("notification-topic", "New order available for worker: " + worker);
                log.info("sended to notification-topic about avaliable topic");
            }
        } else {

            log.info("No available workers found for order: " + order.getId());
        }
        return nearbyWorkers.length;
    }
}
