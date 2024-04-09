package ru.practicum.mainmodule.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.user.model.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class ParticipationRequestDto {

    private final Long id;

    private final LocalDateTime created;

    private final Event event;

    private final User requester;

    private final RequestStatus status;
}
