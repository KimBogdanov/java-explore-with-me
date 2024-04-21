package ru.practicum.mainmodule.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.location.dto.LocationDto;
import ru.practicum.mainmodule.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationDtoMapper {
    Location toModel(LocationDto locationDto);
}
