package ru.practicum.mainmodule.category.service;

import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.dto.CategoryShortDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(CategoryShortDto categoryShortDto);

    CategoryDto patchCategory(Long categoryId, CategoryShortDto categoryShortDto);

    void deleteCategory(Long categoryId);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);
}
