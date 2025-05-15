package ru.homerep.orderservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private String orderID;
    private String serviceName;
    private String clientName;
    private String clientEmail;
    private String servicePrice;
    private String serviceDate;
    private String employeeName;
    private String employeeEmail;
    private String orderDate;
    private String employeePhone;

}
