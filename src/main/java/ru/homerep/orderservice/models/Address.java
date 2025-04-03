package ru.homerep.orderservice.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String streetName;
    private String buildingNumber;
    private String apartmentNumber;
    private String cityName;
    private Double longitude;
    private Double latitude;

}
