package ru.practicum.mainmodule.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.mainmodule.compilation.dto.NewCompilationDto;
import ru.practicum.mainmodule.compilation.model.Compilation;
import ru.practicum.mainmodule.event.model.Event;

import java.util.List;


@Mapper(componentModel = "spring")
public interface NewCompilationDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", source = "events")
    @Mapping(target = "pinned", defaultValue = "false")
    Compilation toModel(NewCompilationDto newCompilationDto, List<Event> events);
}
