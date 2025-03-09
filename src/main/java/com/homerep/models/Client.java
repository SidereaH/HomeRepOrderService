package com.homerep.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Table(name = "clients")
@Entity
@AllArgsConstructor
@NoArgsConstructor//(access = AccessLevel.PRIVATE, force = true)
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;
    @OneToMany
    private List<Address> addresses;


}
