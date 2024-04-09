package ru.practicum.mainmodule.event.service;

import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.dto.UpdateEventAdminRequestDto;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto adminPatchEvent(Long eventId, UpdateEventAdminRequestDto updateEventDto);
}
