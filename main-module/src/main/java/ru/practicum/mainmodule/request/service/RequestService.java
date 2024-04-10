package ru.practicum.mainmodule.request.service;

import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequestsForRequester(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestsId);

    List<ParticipationRequestDto> getAllRequestsByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateStatusRequest(
            EventRequestStatusUpdateRequestDto statusUpdateRequestDto, Long userId, Long eventId);
}
