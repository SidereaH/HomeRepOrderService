package ru.homerep.orderservice.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.homerep.orderservice.models.Category;
import ru.homerep.orderservice.repositories.CategoryRepository;
import ru.homerep.orderservice.services.CategoryService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public CategoryController(CategoryRepository categoryRepository, CategoryService categoryService) {
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Iterable<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        boolean isNewCategory = categoryService.saveCategory(category);
        if(isNewCategory) {
            return ResponseEntity.badRequest().body(category);
        }
        return ResponseEntity.ok().body(category);
    }


}
