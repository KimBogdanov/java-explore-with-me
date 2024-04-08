package ru.practicum.mainmodule.event.service;

import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);
}
