package com.homerep.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homerep.models.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(Long id);
    List<Order> findOrdersByCustomerId(Long client_id);
    List<Order> findOrdersByEmployeeId(Long employee_id);
}
