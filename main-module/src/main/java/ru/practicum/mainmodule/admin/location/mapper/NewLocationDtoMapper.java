package ru.practicum.mainmodule.admin.location.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.admin.location.dto.NewLocationDto;
import ru.practicum.mainmodule.admin.location.model.Location;

@Mapper(componentModel = "spring")
public interface NewLocationDtoMapper {
    Location toModel(NewLocationDto newLocationDto);
}
