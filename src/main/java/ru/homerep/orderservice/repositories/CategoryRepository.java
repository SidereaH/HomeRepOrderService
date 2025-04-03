package ru.homerep.orderservice.repositories;

import ru.homerep.orderservice.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByName(String name);
}
