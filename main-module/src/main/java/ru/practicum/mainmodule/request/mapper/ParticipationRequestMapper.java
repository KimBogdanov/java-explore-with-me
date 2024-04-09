package ru.practicum.mainmodule.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;
import ru.practicum.mainmodule.request.model.Request;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {
    ParticipationRequestDto toDto(Request request);
}
