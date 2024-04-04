package ru.practicum.mainmodule.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.category.model.Category;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.model.Event;

@Mapper(componentModel = "spring")
public interface NewEventDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "description", source = "newEventDto.description")
    @Mapping(target = "paid", source = "newEventDto.paid", defaultValue = "false")
    @Mapping(target = "participantLimit", source = "newEventDto.participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "newEventDto.requestModeration", defaultValue = "true")
    Event toEvent(NewEventDto newEventDto, Category category, Location location);
}
