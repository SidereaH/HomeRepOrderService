package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.homerep.models.Employer;

public interface EmployerRepository extends CrudRepository<Employer, Long> {
    Employer findById(long id);
    Employer findByEmail(String email);
    Employer findByPhone(String phone);
}
