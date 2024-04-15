package ru.practicum.mainmodule.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @GetMapping()
    public List<EventFullDto> getAllEventsForPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<@Positive Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("getAllEventsForPublic with text: {} sort: {} categories ids: {} rangeStart: {} rangeEnd {}",
                text, sort, categories, rangeEnd, rangeEnd);
        checkDateAndThrowException(rangeStart, rangeEnd);
        sort = (sort == null || sort.equals("EVENT_DATE")) ? "eventDate" : "id";
        return eventService.getAllEventsForPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request
        );
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventForPublic(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("getEventForPublic event id: {}", eventId);
        return eventService.getEventForPublic(eventId, request);
    }

    private void checkDateAndThrowException(LocalDateTime start, LocalDateTime end) {
        if ((start != null && end != null) && start.isAfter(end)) {
            throw new IllegalArgumentException("Start after end");
        }
    }
}
