package ru.homerep.orderservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor//(access = AccessLevel.PRIVATE, force = true)
@Table(name = "orders")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne
    private Category category;
//    @ManyToOne
//    private Client client;
    private Long customerId;
    private Long employeeId;
    @ManyToOne
    private Address address;
    @ManyToOne
    private PaymentType paymentType;
    private Boolean accepted = false;

}
