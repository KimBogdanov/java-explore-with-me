package ru.practicum.mainmodule.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.location.dto.LocationFullDto;
import ru.practicum.mainmodule.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationFullDtoMapper {
    LocationFullDto toDto(Location location);
}
