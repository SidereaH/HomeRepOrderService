package ru.homerep.orderservice.repositories;

import ru.homerep.orderservice.models.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long> {
    City findByName(String name);
}
