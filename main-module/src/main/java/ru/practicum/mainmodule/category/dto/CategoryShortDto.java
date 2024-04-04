package ru.practicum.mainmodule.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryShortDto {
    @NotBlank
    private String name;
}
