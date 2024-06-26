package ru.practicum.mainmodule.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;
import ru.practicum.mainmodule.request.model.Request;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto toDto(Request request);
}
