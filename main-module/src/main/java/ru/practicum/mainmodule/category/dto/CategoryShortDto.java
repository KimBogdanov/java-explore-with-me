package ru.practicum.mainmodule.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryShortDto {
    @NotBlank
    @Size(max = 50)
    private String name;
}
