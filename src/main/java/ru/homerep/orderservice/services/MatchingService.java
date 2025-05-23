package ru.homerep.orderservice.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import ru.homerep.orderservice.config.HomeRepProperties;
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
    private final RestTemplate restTemplate;

    private final String USER_SERVICE_URL;
    public MatchingService(
//            WorkerRepository workerRepository,
            KafkaTemplate<String, OrderRequest> kafkaTemplate,
            LocationServiceClient locationServiceClient, ObjectMapper objectMapper, RestTemplate restTemplate, HomeRepProperties props) {
//        this.workerRepository = workerRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.locationServiceClient = locationServiceClient;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        USER_SERVICE_URL = props.getUserservice() + "/clients";
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "order-topic", groupId = "matching-service")
    public Integer findWorker(String orderJson) throws JsonProcessingException {
        Order order = objectMapper.readValue(orderJson, Order.class);
        Double lat = order.getAddress().getLatitude();
        Double lon = order.getAddress().getLongitude();
        String userMail = getUserEmail(order.getCustomerId());
        //среди всех работников найти ближайших через микросервис userservice
        long[] nearbyWorkers = locationServiceClient.getUsersByLatLng(lat, lon,10 );

        log.info("Nearby workers found for order for user "+order.getCustomerId()+": " + order.getId() + ": " + nearbyWorkers.length + " nearby");
        if (nearbyWorkers.length > 0) {
            for (long worker : nearbyWorkers) {
                if (isUserWorker(worker)) {
                    String employeeMail =  getUserEmail(worker);

                    kafkaTemplate.send("order-available-topic", new OrderRequest(order.getId().toString(),order.getCategory().getName(), null, userMail, null, order.getCreatedAt().toString(), null,employeeMail,order.getCreatedAt().toString(),null));
                    log.info("sended to notification-topic about avaliable topic");
                }
               log.warn("user %d is not worker", worker);
            }
        } else {

            log.info("No available workers found for order: " + order.getId());
        }
        return nearbyWorkers.length;
    }

    private String getUserEmail(Long userId) {
        if (userId != null)   {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId + "/mail", String.class);

        }
        else return "hutornoyaa@gmail.com";
    }
    private boolean isUserWorker(Long userId) {
        if (userId != null)   {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId + "/status" , Boolean.class);
        }
        return false;
    }
}
