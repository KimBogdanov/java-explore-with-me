package ru.practicum.mainmodule.event.service;

import ru.practicum.mainmodule.event.dto.*;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.model.enums.SearchEventValues;

import javax.servlet.http.HttpServletRequest;
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

    EventFullDto patchEventForUser(Long userId, Long eventId, UpdateEventUserRequestDto eventUserRequestDto);

    List<EventFullDto> getAllEventsForPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            SearchEventValues sort,
            Integer from,
            Integer size,
            HttpServletRequest request);

    EventFullDto getEventForPublic(Long eventId, HttpServletRequest request);

    List<EventShortDto> getAllEventsByLocation(Long locationId, Integer from, Integer size);
}
