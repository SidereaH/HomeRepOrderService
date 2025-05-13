package ru.homerep.orderservice.services;

import org.springframework.stereotype.Service;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.repositories.CategoryRepository;

import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public boolean saveCategory(Category category) {
        String categoryName = category.getName();

        if(categoryRepository.findByName(categoryName).isPresent()) {
            return false;
        }
        categoryRepository.save(category);

        return true;
    }
    public boolean deleteCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
