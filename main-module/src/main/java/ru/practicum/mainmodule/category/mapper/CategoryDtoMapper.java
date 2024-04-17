package ru.practicum.mainmodule.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryDtoMapper {
    CategoryDto toCategory(Category category);
}
