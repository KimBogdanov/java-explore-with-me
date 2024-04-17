package ru.practicum.mainmodule.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryRepository;

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        log.info("Get category id: {}", categoryId);
        return categoryRepository.getCategoryById(categoryId);
    }

    @GetMapping()
    public List<CategoryDto> getAllCategories(
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Get all categories from: {}, size {}", from, size);
        return categoryRepository.getAllCategories(from, size);
    }

}
