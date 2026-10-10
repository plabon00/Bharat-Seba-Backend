package com.learningportal.category.service;

import com.learningportal.category.dto.CategoryResponse;
import com.learningportal.category.entity.Category;
import com.learningportal.category.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"))
                .stream()
                .filter(Category::isActive)
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
