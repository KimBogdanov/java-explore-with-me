package ru.practicum.mainmodule.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;
import ru.practicum.mainmodule.request.enums.RequestStatus;
import ru.practicum.mainmodule.request.mapper.ParticipationRequestMapper;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.repository.RequestRepository;
import ru.practicum.mainmodule.user.model.User;
import ru.practicum.mainmodule.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        User user = getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);

        checkIfUserIsEventInitiatorAndThrowException(userId, event.getInitiator().getId());
        checkIfEventIsPublishedAndThrowException(eventId, event.getState());
        checkIfParticipantLimitFullAndThrowException(eventId, event);

        return Optional.of(getRequest(user, event))
                .map(requestRepository::save)
                .map(participationRequestMapper::toDto)
                .get();
    }

    private void checkIfParticipantLimitFullAndThrowException(Long eventId, Event event) {
        if (event.getParticipantLimit() != 0 &&
                requestRepository.countAllByEvent_Id(eventId).equals(event.getParticipantLimit())) {
            throw new DataIntegrityViolationException(
                    String.format("The limit of participants has been reached for the event with id=%d", eventId)
            );
        }
    }

    private static void checkIfEventIsPublishedAndThrowException(Long eventId, EventState state) {
        if (!state.equals(EventState.PUBLISHED)) {
            throw new DataIntegrityViolationException(
                    String.format("Event with id=%d is not published", eventId)
            );
        }
    }

    private void checkIfUserIsEventInitiatorAndThrowException(Long userId, Long eventId) {
        if (eventId.equals(userId)) {
            throw new DataIntegrityViolationException(
                    String.format("User with id=%d is initiator event id=%d", userId, eventId)
            );
        }
    }

    private Request getRequest(User user, Event event) {
        return Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED).build();
    }

    private Event getEventOrThrowNotFoundException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private User getUserOrThrowNotFoundException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId))
        );
    }
}
