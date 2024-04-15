package ru.practicum.mainmodule.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.category.dto.CategoryShortDto;
import ru.practicum.mainmodule.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryShortDtoMapper {
    Category toCategory(CategoryShortDto categoryShortDto);
}
