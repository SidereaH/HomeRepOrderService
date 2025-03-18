package com.homerep.repositories;

import org.springframework.data.repository.CrudRepository;
import com.homerep.models.Category;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByName(String name);
}
