package ru.practicum.mainmodule.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.location.mapper.LocationDtoMapper;
import ru.practicum.mainmodule.category.mapper.CategoryDtoMapper;
import ru.practicum.mainmodule.event.dto.EventShortDto;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.user.mapper.UserShortDtoMapper;

@Mapper(componentModel = "spring", uses = {CategoryDtoMapper.class, LocationDtoMapper.class, UserShortDtoMapper.class})
public interface EventShortMapper {
    EventShortDto toDto(Event event, Integer confirmedRequests, Long views);
}
