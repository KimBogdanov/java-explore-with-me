package ru.practicum.mainmodule.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.service.EventService;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    public EventFullDto saveEvent(@RequestBody @Valid NewEventDto newEventDto,
                                  Long userId) {
        log.info("saveEvent title {}", newEventDto.getTitle());
        return eventService.saveEvent(userId, newEventDto);
    }
}
