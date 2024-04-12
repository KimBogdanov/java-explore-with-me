package ru.practicum.mainmodule.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.service.LocationService;
import ru.practicum.mainmodule.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.request.repository.RequestRepository;
import ru.practicum.mainmodule.user.model.User;
import ru.practicum.mainmodule.category.model.Category;
import ru.practicum.mainmodule.category.repository.CategoryRepository;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.mapper.EventFullDtoMapper;
import ru.practicum.mainmodule.event.mapper.NewEventDtoMapper;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.ConditionsNotMetException;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.user.repository.UserRepository;
import ru.practicum.mainmodule.util.DateTimeUtil;
import ru.practicum.mainmodule.util.PageRequestFrom;
import ru.practicum.statisticservice.StatisticClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        //получаем список всех event id
        log.info("получаем список всех event id");
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        //получаем все реквесты для нужных эвентов и считаем их кол-во группируя по event id
        log.info("получаем все реквесты для нужных эвентов и считаем их кол-во группируя по event id");
        List<Request> allByEventIdInAndStatus = requestRepository.findAllByEventIdInAndStatus(eventIds,
                RequestStatus.CONFIRMED);


        Map<Long, Integer> countRequestsByEventId = allByEventIdInAndStatus.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue))
                );
        log.info("получаем все uris для запроса статистики");
        //получаем все uris для запроса статистики
        List<String> uris = eventIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());
        //получаем статистику
        log.info("получаем статистику");
        ResponseEntity<Object> stats = statisticClient.getStats(rangeStart, rangeEnd, uris, false);
        List<ReadStatisticDto> statisticDtos;
        if (stats.getStatusCode().is2xxSuccessful()) {
            log.info("преобразуем к списку readStatisticDto");
            //преобразуем к списку readStatisticDto
            statisticDtos = objectMapper.convertValue(stats.getBody(), new TypeReference<>() {
            });
        } else {
            throw new RuntimeException(Objects.requireNonNull(stats.getBody()).toString());
        }
        Map<Long, Long> statisticMap = statisticDtos.stream()
                .collect(Collectors.toMap(
                        dto -> extractIdFromUri(dto.getUri()),
                        ReadStatisticDto::getHits)
                );
        log.info("Заканчиваем метод");
        return events.stream()
                .map(event -> eventFullDtoMapper.toDto(
                        event,
                        countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
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
}
