package ru.practicum.mainmodule.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.ConflictException;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.mainmodule.request.dto.EventRequestStatusUpdateResultDto;
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
        checkIfParticipantLimitFullAndThrowException(event);

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
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        return requestRepository.findAllByEventId(eventId).stream()
                .map(participationRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResultDto updateStatusRequest(
            EventRequestStatusUpdateRequestDto statusUpdateRequestDto, Long userId, Long eventId) {
        /*Очень большой метод получился, возможно я неправильно понял ТЗ, напишу для себя что делаю.
        Тз такое
        если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
        */
        //проверяю существует ли user, event. Может ли user модерировать заявки на event
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        //если подтверждаем
        if (statusUpdateRequestDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            // и есть лимит, то надо проверить кол-во подтвержденных
            if (event.getParticipantLimit() != 0) {
                //Получаю число подтвержденных заявок + которые подтвержу сейчас
                Integer countRequestsLimit = requestRepository.countAllByEventIdAndStatus(event.getId(),
                        RequestStatus.CONFIRMED) + statusUpdateRequestDto.getRequestIds().size();
                //проверяю не превысит ли число заявок
                if (countRequestsLimit > event.getParticipantLimit()) {
                    throw new ConflictException("The participant limit has been reached");
                }

                //Получаю все заявки в которых надо изменить статус, проверяю, что у них статус Pending, меняю на необходимый
                // и сохраняю в базу
                List<Request> requests = updateStatusRequest(statusUpdateRequestDto, eventId);
                //Создаю возвращаемый объект
                EventRequestStatusUpdateResultDto result = EventRequestStatusUpdateResultDto.builder()
                        .confirmedRequests(requests.stream()
                                .map(participationRequestMapper::toDto)
                                .collect(Collectors.toList())).build();

                //Если число мест на мероприятии закончилось, то получаю все оставшиеся заявки в статусе ожидания и отменяю их.
                if (countRequestsLimit.equals(event.getParticipantLimit())) {
                    List<Request> otherRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                    otherRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                    requestRepository.saveAll(otherRequests);
                    //дополняю возвращаемый объект
                    result.setRejectedRequests(otherRequests.stream()
                            .map(participationRequestMapper::toDto)
                            .collect(Collectors.toList()));

                }
                return result;

            } else {
                //если лимита нет, то в возвращаемом объекте не будет записей с отмененными записями
                List<Request> requests = updateStatusRequest(statusUpdateRequestDto, eventId);
                return EventRequestStatusUpdateResultDto.builder()
                        .confirmedRequests(requests.stream()
                                .map(participationRequestMapper::toDto)
                                .collect(Collectors.toList())).build();
            }

        } else {

            //Если отменяю, то в возвращаемом объекте не будет записей с подтвержденными
            List<Request> rejectedRequest = updateStatusRequest(statusUpdateRequestDto, eventId);

            return EventRequestStatusUpdateResultDto.builder()
                    .rejectedRequests(rejectedRequest.stream()
                            .map(participationRequestMapper::toDto)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    private List<Request> updateStatusRequest(EventRequestStatusUpdateRequestDto statusUpdateRequestDto, Long eventId) {
        List<Request> requests = requestRepository.findAllByIdInAndEventId(statusUpdateRequestDto.getRequestIds(),
                eventId);
        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }
            request.setStatus(statusUpdateRequestDto.getStatus());
        });
        requestRepository.saveAll(requests);
        return requests;
    }

    private void checkIfUserIsEventOwnerAndThrowException(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of event id=%d", userId, event.getId())
            );
        }
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

    private void checkIfParticipantLimitFullAndThrowException(Event event) {
        if (event.getParticipantLimit() != 0 &&
                requestRepository.countAllByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                        .equals(event.getParticipantLimit())) {
            throw new ConflictException(
                    String.format("The limit of participants has been reached for the event with id=%d", event.getId())
            );
        }
    }

    private static void checkIfEventIsPublishedAndThrowException(Long eventId, EventState state) {
        if (!state.equals(EventState.PUBLISHED)) {
            throw new ConflictException(
                    String.format("Event with id=%d is not published", eventId)
            );
        }
    }

    private void checkIfUserIsEventInitiatorAndThrowException(Long userId, Long eventId) {
        if (eventId.equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d is initiator event id=%d", userId, eventId)
            );
        }
    }

    private Request createRequest(User user, Event event) {
        return Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status((event.getRequestModeration() && event.getParticipantLimit() != 0) ?
                        RequestStatus.PENDING : RequestStatus.CONFIRMED).build();
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
