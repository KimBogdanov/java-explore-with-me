package ru.practicum.mainmodule.request.service;

import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto saveRequest(Long userId, Long eventId);
}
