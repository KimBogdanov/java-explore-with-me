package ru.practicum.mainmodule.category.service;

import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.dto.CategoryShortDto;
import ru.practicum.mainmodule.category.dto.CategoryUpdateDto;

import javax.validation.Valid;
import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(CategoryShortDto categoryShortDto);

    CategoryDto patchCategory(Long categoryId, @Valid CategoryUpdateDto categoryShortDto);

    void deleteCategory(Long categoryId);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAllCategories(Integer from, Integer size);
}
