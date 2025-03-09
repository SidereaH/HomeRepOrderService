package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.homerep.models.Client;
import ru.homerep.models.Employee;
import ru.homerep.models.Employer;
import ru.homerep.models.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Order findOrderById(Long id);
    List<Order> findOrdersByClient(Client client);
    List<Order> findOrdersByEmployee(Employee employee);
    List<Order> findOrdersByEmployer(Employer employer);
}
