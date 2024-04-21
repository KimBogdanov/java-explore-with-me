package ru.practicum.mainmodule.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.location.dto.NewLocationDto;
import ru.practicum.mainmodule.location.model.Location;

@Mapper(componentModel = "spring")
public interface NewLocationDtoMapper {
    Location toModel(NewLocationDto newLocationDto);
}
