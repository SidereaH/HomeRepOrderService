package ru.homerep.orderservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssignResponse {
    private String message;
    private Long orderId;
    private Long workerId;


}
