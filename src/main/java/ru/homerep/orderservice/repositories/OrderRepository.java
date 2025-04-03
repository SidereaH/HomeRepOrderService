package ru.homerep.orderservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.homerep.orderservice.models.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(Long id);
    List<Order> findOrdersByCustomerId(Long client_id);
    List<Order> findOrdersByEmployeeId(Long employee_id);
}
