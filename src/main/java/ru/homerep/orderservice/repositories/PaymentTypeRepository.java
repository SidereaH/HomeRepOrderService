package ru.homerep.orderservice.repositories;

import ru.homerep.orderservice.models.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
    Optional<PaymentType> findByName(String name);

}
