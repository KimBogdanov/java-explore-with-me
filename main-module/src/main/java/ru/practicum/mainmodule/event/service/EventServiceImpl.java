package ru.practicum.mainmodule.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.service.LocationService;
import ru.practicum.mainmodule.event.dto.*;
import ru.practicum.mainmodule.event.mapper.EventShortMapper;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.model.enums.StateAction;
import ru.practicum.mainmodule.exception.ConflictException;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.request.repository.RequestRepository;
import ru.practicum.mainmodule.user.model.User;
import ru.practicum.mainmodule.category.model.Category;
import ru.practicum.mainmodule.category.repository.CategoryRepository;
import ru.practicum.mainmodule.event.mapper.EventFullDtoMapper;
import ru.practicum.mainmodule.event.mapper.NewEventDtoMapper;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.ConditionsNotMetException;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.user.repository.UserRepository;
import ru.practicum.mainmodule.util.DateTimeUtil;
import ru.practicum.mainmodule.util.PageRequestFrom;
import ru.practicum.statisticservice.StatisticClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationService locationService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final NewEventDtoMapper newEventDtoMapper;
    private final EventFullDtoMapper eventFullDtoMapper;
    private final EventShortMapper eventShortMapper;
    private final StatisticClient statisticClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public EventFullDto saveEvent(Long userId, NewEventDto newEventDto) {
        User user = getUserOrThrowNotFoundException(userId);

        checkTimeThrowNotCorrectTimeException(newEventDto.getEventDate(), 2);

        Category category = getCategoryOrThrowNotFoundException(newEventDto.getCategory());

        Location location = locationService.save(newEventDto.getLocation());

        return Optional.of(newEventDto)
                .map(dto -> newEventDtoMapper.toEvent(dto, category, location, user))
                .map(eventRepository::save)
                .map(event -> eventFullDtoMapper.toDto(event, 0, 0L))
                .get();
    }

    @Override
    @Transactional
    public EventFullDto adminPatchEvent(Long eventId, UpdateEventAdminRequestDto updateEventDto) {
        Event event = getEventOrThrowNotFoundException(eventId);

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            event.setCategory(getCategoryOrThrowNotFoundException(updateEventDto.getCategory()));
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            checkTimeThrowNotCorrectTimeException(updateEventDto.getEventDate(), 2);
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(locationService.save(updateEventDto.getLocation()));
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING)) {
                switch (updateEventDto.getStateAction()) {
                    case PUBLISH_EVENT:
                        checkTimeThrowNotCorrectTimeException(event.getEventDate(), 1);
                        event.setState(EventState.PUBLISHED);
                        break;
                    case REJECT_EVENT:
                        event.setState(EventState.CANCELED);
                        break;
                }
            } else {
                throw new ConditionsNotMetException(
                        String.format("Cannot publish the event because it's not in the right state: %s",
                                event.getState()));
            }
        }
        event.setPublishedOn(LocalDateTime.now());
        return eventFullDtoMapper.toDto(event, 0, 0L);
    }

    @Override
    public EventFullDto patchEventForUser(Long userId, Long eventId,
                                          UpdateEventUserRequestDto eventUserRequestDto) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);

        if (eventUserRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventUserRequestDto.getAnnotation());
        }
        if (eventUserRequestDto.getCategory() != null) {
            event.setCategory(getCategoryOrThrowNotFoundException(eventUserRequestDto.getCategory()));
        }
        if (eventUserRequestDto.getDescription() != null) {
            event.setDescription(eventUserRequestDto.getDescription());
        }
        if (eventUserRequestDto.getEventDate() != null) {
            checkTimeThrowNotCorrectTimeException(eventUserRequestDto.getEventDate(), 2);
            event.setEventDate(eventUserRequestDto.getEventDate());
        }
        if (eventUserRequestDto.getLocation() != null) {
            event.setLocation(locationService.save(eventUserRequestDto.getLocation()));
        }
        if (eventUserRequestDto.getPaid() != null) {
            event.setPaid(eventUserRequestDto.getPaid());
        }
        if (eventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUserRequestDto.getParticipantLimit());
        }
        if (eventUserRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUserRequestDto.getRequestModeration());
        }
        if (eventUserRequestDto.getTitle() != null) {
            event.setTitle(eventUserRequestDto.getTitle());
        }
        if (eventUserRequestDto.getStateAction() != null) {
            if (event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED)) {
                switch (eventUserRequestDto.getStateAction()) {
                    case SEND_TO_REVIEW:
                        event.setState(EventState.PENDING);
                        break;
                    case CANCEL_REVIEW:
                        event.setState(EventState.CANCELED);
                }
            } else {
                throw new ConditionsNotMetException(
                        String.format("Cannot publish the event because it's not in the right state: %s",
                                event.getState()));
            }
        }

        return eventFullDtoMapper.toDto(event, 0, 0L);
    }

    @Override
    public List<EventFullDto> getAllEventsForAdmin(List<Long> users,
                                                   List<EventState> states,
                                                   List<Long> categories,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Integer from,
                                                   Integer size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().minusYears(100);
        }

        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        Page<Event> events;
        log.info("Get events");
        if (users == null) {
            if (states == null) {
                if (categories == null) {
                    log.info("1");
                    events = eventRepository.findAllByEventDateBetween(
                            rangeStart,
                            rangeEnd,
                            new PageRequestFrom(from, size, null)
                    );
                } else {
                    log.info("2");
                    events = eventRepository.findAllByEventDateBetweenAndCategoryIdIn(
                            rangeStart,
                            rangeEnd,
                            categories,
                            new PageRequestFrom(from, size, null)
                    );
                }
            } else {
                if (categories == null) {
                    log.info("3");
                    events = eventRepository.findAllByEventDateBetweenAndStateIn(
                            rangeStart,
                            rangeEnd,
                            states,
                            new PageRequestFrom(from, size, null)
                    );
                } else {
                    log.info("4");
                    events = eventRepository.findAllByEventDateBetweenAndCategoryIdInAndStateIn(
                            rangeStart,
                            rangeEnd,
                            categories,
                            states,
                            new PageRequestFrom(from, size, null)
                    );
                }
            }
        } else {
            if (states == null) {
                if (categories == null) {
                    log.info("5");
                    events = eventRepository.findAllByEventDateBetweenAndInitiatorIdIn(
                            rangeStart,
                            rangeEnd,
                            users,
                            new PageRequestFrom(from, size, null)
                    );
                } else {
                    log.info("6");
                    events = eventRepository.findAllByEventDateBetweenAndCategoryIdInAndInitiatorIdIn(
                            rangeStart,
                            rangeEnd,
                            categories,
                            users,
                            new PageRequestFrom(from, size, null)
                    );
                }

            } else {
                if (categories == null) {
                    log.info("7");
                    events = eventRepository.findAllByEventDateBetweenAndStateInAndInitiatorIdIn(
                            rangeStart,
                            rangeEnd,
                            states,
                            users,
                            new PageRequestFrom(from, size, null)
                    );

                } else {
                    log.info("8");
                    events = eventRepository.findAllByEventDateBetweenAndCategoryIdInAndStateInAndInitiatorIdIn(
                            rangeStart,
                            rangeEnd,
                            categories,
                            states,
                            users,
                            new PageRequestFrom(from, size, null)
                    );
                }

            }
        }

        List<Long> eventsIds = getEventsId(events);
        Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
        Map<Long, Long> statisticMap = getStatisticMap(rangeStart, rangeEnd, eventsIds);

        return events.stream()
                .map(event -> eventFullDtoMapper.toDto(
                        event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventsForOwner(Long userId, Integer from, Integer size) {
        getUserOrThrowNotFoundException(userId);
        Page<Event> events = eventRepository.findAllByInitiatorId(userId, new PageRequestFrom(from, size, null));
        log.info("List<Long> eventsIds = getEventsId(events)");
        List<Long> eventsIds = getEventsId(events);
        log.info("Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds)");
        Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
        log.info("getStatisticMap(LocalDateTime.now().minusYears(100),\n" +
                "                LocalDateTime.now().plusYears(100), eventsIds);");
        Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);
        log.info("return");
        return events.stream()
                .map(event -> eventShortMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventForOwner(Long userId, Long eventId) {
        getUserOrThrowNotFoundException(userId);
        Event event = getEventOrThrowNotFoundException(eventId);
        checkIfUserIsEventOwnerAndThrowException(event, userId);

        Integer countRequest = requestRepository.countAllRequestByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), List.of(eventId));

        return eventFullDtoMapper.toDto(event, countRequest, statisticMap.get(eventId));
    }

    @Override
    public List<EventFullDto> getAllEventsForPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest request) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        log.info("Get events");
        Page<Event> events = eventRepository.getAllEventsForPublic(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                EventState.PUBLISHED,
                new PageRequestFrom(from, size, Sort.by(sort))
        );

        log.info("get eventsId");
        List<Long> eventsIds = getEventsId(events);
        Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
        Map<Long, Long> statisticMap = getStatisticMap(rangeStart, rangeEnd, eventsIds);


        statisticClient.saveHit(CreateStatisticDto.builder()
                .app("main")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now()).build());


        List<EventFullDto> collect = events.stream()
                .map(event -> eventFullDtoMapper.toDto(
                        event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());
        if (sort.equals("id")) {
            return collect.stream()
                    .sorted(Comparator.comparingLong(EventFullDto::getViews))
                    .collect(Collectors.toList());
        }
        return collect;
    }

    @Override
    public EventFullDto getEventForPublic(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Integer countRequest = requestRepository.countAllRequestByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), List.of(eventId));

        statisticClient.saveHit(CreateStatisticDto.builder()
                .app("main")
                .uri("/events/" + eventId)
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now()).build());

        return eventFullDtoMapper.toDto(event, countRequest, statisticMap.get(eventId));
    }

    private Map<Long, Long> getStatisticMap(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> eventsIds) {
        List<String> uris = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> stats = statisticClient.getStats(rangeStart, rangeEnd, uris, false);
        List<ReadStatisticDto> statisticDtos;
        if (stats.getStatusCode().is2xxSuccessful()) {
            log.info("преобразуем к списку readStatisticDto");
            statisticDtos = objectMapper.convertValue(stats.getBody(), new TypeReference<>() {
            });
        } else {
            throw new RuntimeException(Objects.requireNonNull(stats.getBody()).toString());
        }
        log.info("return");

        return statisticDtos.stream()
                .collect(Collectors.toMap(
                        dto -> extractIdFromUri(dto.getUri()),
                        ReadStatisticDto::getHits)
                );
    }

    private static List<Long> getEventsId(Page<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }

    private Event getEventOrThrowNotFoundException(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private void checkTimeThrowNotCorrectTimeException(LocalDateTime dateTime, Integer hour) {
        if (dateTime.isBefore(LocalDateTime.now().plusHours(hour))) {
            throw new ConditionsNotMetException(
                    String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                            DateTimeUtil.formatLocalDateTime(dateTime)));
        }
    }

    private Category getCategoryOrThrowNotFoundException(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%d was not found", categoryId))
        );
    }

    private User getUserOrThrowNotFoundException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId))
        );
    }

    private Map<Long, Integer> getCountByEventId(List<Long> eventIds) {

        log.info("получаем все реквесты для нужных эвентов и считаем их кол-во группируя по event id");
        List<Request> allByEventIdInAndStatus = requestRepository.findAllByEventIdInAndStatus(eventIds,
                RequestStatus.CONFIRMED);

        return allByEventIdInAndStatus.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue))
                );
    }

    private void checkIfUserIsEventOwnerAndThrowException(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("User with id=%d not owner of event id=%d", userId, event.getId())
            );
        }
    }
}
