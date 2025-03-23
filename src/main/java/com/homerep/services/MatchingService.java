package com.homerep.services;


import com.homerep.models.Order;
import com.homerep.models.Worker;
import com.homerep.repository.WorkerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchingService {
    private final WorkerRepository workerRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public MatchingService(WorkerRepository workerRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.workerRepository = workerRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-topic", groupId = "matching-group")
    public void findWorker(Order order) {
        Double lat = order.getAddress().getLatitude();
        Double lon = order.getAddress().getLongitude();

        List<Worker> nearbyWorkers = workerRepository.findByLatitudeBetweenAndLongitudeBetween(
                lat - 0.05, lat + 0.05, lon - 0.05, lon + 0.05
        );

        if (!nearbyWorkers.isEmpty()) {
            for (Worker worker : nearbyWorkers) {
                kafkaTemplate.send("notification-topic", "New order available for worker: " + worker.getId());
            }
        } else {
            System.out.println("No available workers found for order: " + order.getId());
        }
    }
}
