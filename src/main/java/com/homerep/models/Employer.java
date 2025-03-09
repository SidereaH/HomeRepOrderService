package com.homerep.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String phone;

}
