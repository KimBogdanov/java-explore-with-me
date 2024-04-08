package ru.practicum.mainmodule.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.service.LocationService;
import ru.practicum.mainmodule.admin.model.User;
import ru.practicum.mainmodule.category.model.Category;
import ru.practicum.mainmodule.category.repository.CategoryRepository;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.NewEventDto;
import ru.practicum.mainmodule.event.mapper.EventFullDtoMapper;
import ru.practicum.mainmodule.event.mapper.NewEventDtoMapper;
import ru.practicum.mainmodule.event.model.enums.EventState;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.NotCorrectTimeException;
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

        checkTimeThrowNotCorrectTimeException(newEventDto);

        Category category = getCategoryOrThrowNotFoundException(newEventDto);

        Location location = locationService.save(newEventDto.getLocation());

        return Optional.of(newEventDto)
                .map(dto -> newEventDtoMapper.toEvent(dto, category, location, user))
                .map(eventRepository::save)
                .map(event -> eventFullDtoMapper.toDto(event, 0, 0L))
                .get();
    }

    private static void checkTimeThrowNotCorrectTimeException(NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotCorrectTimeException(
                    String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                            DateTimeUtil.formatLocalDateTime(newEventDto.getEventDate())));
        }
    }

    private Category getCategoryOrThrowNotFoundException(NewEventDto newEventDto) {
        return categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id=%d was not found", newEventDto.getCategory()))
        );
    }

    private User getUserOrThrowNotFoundException(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id=%d was not found", userId))
        );
    }
}
