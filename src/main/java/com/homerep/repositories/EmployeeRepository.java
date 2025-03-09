package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.homerep.models.Employee;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    Employee findById(long id);
    Employee findByEmail(String email);
    Employee findByPhone(String phone);
    List<Employee> findEmployeesByFirstNameAndLastName(String firstName, String lastName);
}
