package ru.practicum.mainmodule.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class ParticipationRequestDto {
    private final Long id;
    private final LocalDateTime created;
    private final Long event;
    private final Long requester;
    private final RequestStatus status;
}
