package ru.homerep.orderservice.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequest that = (OrderRequest) o;
        return Objects.equals(orderID, that.orderID) && Objects.equals(serviceName, that.serviceName) && Objects.equals(clientName, that.clientName) && Objects.equals(clientEmail, that.clientEmail) && Objects.equals(servicePrice, that.servicePrice) && Objects.equals(serviceDate, that.serviceDate) && Objects.equals(employeeName, that.employeeName) && Objects.equals(employeeEmail, that.employeeEmail) && Objects.equals(orderDate, that.orderDate) && Objects.equals(employeePhone, that.employeePhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, serviceName, clientName, clientEmail, servicePrice, serviceDate, employeeName, employeeEmail, orderDate, employeePhone);
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "orderID='" + orderID + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", clientName='" + clientName + '\'' +
                ", clientEmail='" + clientEmail + '\'' +
                ", servicePrice='" + servicePrice + '\'' +
                ", serviceDate='" + serviceDate + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeEmail='" + employeeEmail + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", employeePhone='" + employeePhone + '\'' +
                '}';
    }
}
