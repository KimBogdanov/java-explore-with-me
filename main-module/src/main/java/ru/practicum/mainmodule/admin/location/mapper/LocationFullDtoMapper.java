package ru.practicum.mainmodule.admin.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.admin.location.dto.LocationFullDto;
import ru.practicum.mainmodule.admin.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationFullDtoMapper {
    LocationFullDto toDto(Location location);
}
