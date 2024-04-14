package ru.practicum.mainmodule.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainmodule.compilation.dto.CompilationDto;
import ru.practicum.mainmodule.compilation.model.Compilation;
import ru.practicum.mainmodule.event.dto.EventShortDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationDtoMapper {
    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation, List<EventShortDto> events);
}
