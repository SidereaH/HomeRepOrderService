package ru.homerep.orderservice.repositories;

import ru.homerep.orderservice.models.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {

}
