package ru.practicum.mainmodule.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.service.EventService;

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
    @GetMapping("/events")
    public List<EventFullDto> getEventsForAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size){
        log.info("getEventsForAdmin for users ids: {} states: {} categories ids: {}", users, states, categories);
        return eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
