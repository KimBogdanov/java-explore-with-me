package ru.practicum.mainmodule.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.mainmodule.event.dto.EventShortDto;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private Long id;
    private Boolean pinned;
    private String title;
    private List<EventShortDto> events;
}
