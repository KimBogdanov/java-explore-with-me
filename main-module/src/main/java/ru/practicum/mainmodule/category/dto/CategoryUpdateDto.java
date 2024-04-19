package ru.practicum.mainmodule.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDto {
    @Size(max = 50)
    private String name;
}
