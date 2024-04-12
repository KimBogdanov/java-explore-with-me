package ru.practicum.mainmodule.event.service;

import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.EventShortDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.mainmodule.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto saveEvent(Long userId, NewEventDto newEventDto);

    EventFullDto adminPatchEvent(Long eventId, UpdateEventAdminRequestDto updateEventDto);

    List<EventFullDto> getAllEventsForAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Integer from,
                                            Integer size);

    List<EventShortDto> getAllEventsForOwner(Long userId, Integer from, Integer size);

    EventFullDto getEventForOwner(Long userId, Long eventId);
}
