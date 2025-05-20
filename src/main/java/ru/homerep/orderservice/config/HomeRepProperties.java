package ru.homerep.orderservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "homerep")
@Getter
@Setter
public class HomeRepProperties {
    private String yandexgeo;
    private String userservice;
}