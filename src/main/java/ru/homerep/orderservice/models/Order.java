package ru.homerep.orderservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", customerId=" + customerId +
                ", employeeId=" + employeeId +
                ", address=" + address +
                ", paymentType=" + paymentType +
                ", accepted=" + accepted +
                '}';
    }
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
