package ru.homerep.orderservice.config;


import ru.homerep.orderservice.models.Order;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderTopic() {
        return new NewTopic("order-topic", 1, (short) 1);
    }
    @Bean
    public NewTopic notificationTopic() {
        return new NewTopic("notification-topic", 1, (short) 1);
    }

}
