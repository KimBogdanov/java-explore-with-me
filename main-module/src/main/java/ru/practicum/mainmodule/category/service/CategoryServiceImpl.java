package ru.practicum.mainmodule.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.dto.CategoryShortDto;
import ru.practicum.mainmodule.category.mapper.CategoryDtoMapper;
import ru.practicum.mainmodule.category.mapper.CategoryShortDtoMapper;
import ru.practicum.mainmodule.category.repository.CategoryRepository;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.util.PageRequestFrom;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryShortDtoMapper categoryShortDtoMapper;
    private final CategoryDtoMapper categoryDtoMapper;

    @Override
    @Transactional
    public CategoryDto save(CategoryShortDto categoryShortDto) {
        return Optional.of(categoryShortDto)
                .map(categoryShortDtoMapper::toCategory)
                .map(categoryRepository::save)
                .map(categoryDtoMapper::toCategory)
                .get();
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long categoryId, CategoryShortDto categoryShortDto) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", categoryId));
        }
        return save(categoryShortDto);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", categoryId));
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryDtoMapper::toCategory)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", categoryId)));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(new PageRequestFrom(from, size, null))
                .getContent()
                .stream()
                .map(categoryDtoMapper::toCategory)
                .collect(Collectors.toList());
    }
}
