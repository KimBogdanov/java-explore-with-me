package ru.practicum.mainmodule.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.ConflictException;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.request.dto.ParticipationRequestDto;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.request.mapper.ParticipationRequestMapper;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.repository.RequestRepository;
import ru.practicum.mainmodule.user.model.User;
import ru.practicum.mainmodule.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        return Optional.of(createRequest(user, event))
                .map(requestRepository::save)
                .map(participationRequestMapper::toDto)
                .get();
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsForRequester(Long userId) {
        getUserOrThrowNotFoundException(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestsId) {
        getUserOrThrowNotFoundException(userId);
        Request request = getRequestOrThrowNotFoundException(requestsId);
        checkIfRequesterIsOwnerAndThrowException(userId, request.getRequester().getId(), requestsId);

        request.setStatus(RequestStatus.REJECTED);
        return Optional.of(request)
                .map(requestRepository::save)
                .map(participationRequestMapper::toDto)
                .get();
    }

    @Override
    public List<ParticipationRequestDto> getAllRequestsByEventId(Long userId, Long eventId) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of event id=%d", userId, eventId)
            );
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkIfRequesterIsOwnerAndThrowException(Long userId, Long requesterId, Long requestsId) {
        if (!requesterId.equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of request id=%d", userId, requestsId)
            );
        }
    }

    private Request getRequestOrThrowNotFoundException(Long requestsId) {
        return requestRepository.findById(requestsId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Request with id=%d was not found", requestsId))
                );
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

    private Request createRequest(User user, Event event) {
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
