package ru.practicum.mainmodule.admin.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationFullDto {
    private Long id;
    private String name;
    private String description;
    private Double lat;
    private Double lon;
}
