package com.homerep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.homerep.models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(Long id);

}
