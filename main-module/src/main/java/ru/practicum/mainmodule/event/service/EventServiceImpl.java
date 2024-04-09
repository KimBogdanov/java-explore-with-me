package ru.practicum.mainmodule.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.service.LocationService;
import ru.practicum.mainmodule.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;
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

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationService locationService;
    private final EventRepository eventRepository;
    private final NewEventDtoMapper newEventDtoMapper;
    private final EventFullDtoMapper eventFullDtoMapper;

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
