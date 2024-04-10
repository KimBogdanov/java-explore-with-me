package ru.practicum.mainmodule.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.service.EventService;
import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;
import ru.practicum.mainmodule.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping("/{userId}/events")
    public EventFullDto saveEvent(@RequestBody @Valid NewEventDto newEventDto,
                                  @PathVariable Long userId) {
        log.info("saveEvent title: {}", newEventDto.getTitle());
        return eventService.saveEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForRequester(@PathVariable Long userId) {
        log.info("getRequestForRequester for user with id: {}", userId);
        return requestService.getAllRequestsForRequester(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto saveRequest(@RequestParam Long eventId,
                                               @PathVariable Long userId) {
        log.info("saveRequest event id: {}, user id: {}", eventId, userId);
        return requestService.saveRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestsId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestsId) {
        log.info("cancelRequest user with id: {} and request id: {}", userId, requestsId);
        return requestService.cancelRequest(userId, requestsId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getAllRequestsForEventOwner(@PathVariable Long userId,
                                                                     @PathVariable Long eventId) {
        log.info("getAllRequestsForEventOwner for user with id: {} and event id: {}", userId, eventId);
        return requestService.getAllRequestsByEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateStatusRequest(
            @RequestBody @Valid EventRequestStatusUpdateRequestDto statusUpdateRequestDto,
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("patchRequestStatus user with id: {} and event id: {}", userId, eventId);
        return requestService.updateStatusRequest(statusUpdateRequestDto, userId, eventId);
    }
}
