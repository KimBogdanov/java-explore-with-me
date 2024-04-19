package ru.practicum.mainmodule.admin.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationDto {
    @Size(min = 3, max = 50)
    private String name;
    @Size(min = 3, max = 7000)
    private String description;
}
