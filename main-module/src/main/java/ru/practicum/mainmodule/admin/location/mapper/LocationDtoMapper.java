package ru.practicum.mainmodule.admin.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainmodule.admin.location.dto.LocationDto;
import ru.practicum.mainmodule.admin.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationDtoMapper {
    Location toModel(LocationDto locationDto);
}
