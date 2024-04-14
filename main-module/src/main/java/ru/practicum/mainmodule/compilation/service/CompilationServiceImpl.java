package ru.practicum.mainmodule.compilation.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.mainmodule.compilation.dto.CompilationDto;
import ru.practicum.mainmodule.compilation.dto.NewCompilationDto;
import ru.practicum.mainmodule.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainmodule.compilation.mapper.CompilationDtoMapper;
import ru.practicum.mainmodule.compilation.mapper.NewCompilationDtoMapper;
import ru.practicum.mainmodule.compilation.model.Compilation;
import ru.practicum.mainmodule.compilation.repository.CompilationRepository;
import ru.practicum.mainmodule.event.dto.EventShortDto;
import ru.practicum.mainmodule.event.mapper.EventShortMapper;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.repository.EventRepository;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.request.repository.RequestRepository;
import ru.practicum.mainmodule.util.PageRequestFrom;
import ru.practicum.statisticservice.StatisticClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatisticClient statisticClient;
    private final NewCompilationDtoMapper newCompilationDtoMapper;
    private final EventShortMapper eventShortMapper;
    private final CompilationDtoMapper compilationDtoMapper;
    private final ObjectMapper objectMapper;


    @Override
    public CompilationDto getPublicCompilationById(Long compId) {
        Compilation compilation = getCompilationOrThrowException(compId);
        List<Event> events = compilation.getEvents();
        List<Long> eventsIds = getEventsId(events);

        Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
        Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);
        List<EventShortDto> eventDtos = events.stream()
                .map(event -> eventShortMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());

        return Optional.of(compilation)
                .map(compilationRepository::save)
                .map(comp -> compilationDtoMapper.toDto(comp, eventDtos))
                .get();
    }

    @Override
    public List<Compilation> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compilations = compilationRepository.findAllCompilationsOptionalPinned(
                pinned,
                new PageRequestFrom(from, size, null)
        );
        return compilations.getContent();
    }

    @Override
    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events;
        List<EventShortDto> eventDtos;
        if (newCompilationDto != null && newCompilationDto.getEvents().size() > 0) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            List<Long> eventsIds = getEventsId(events);
            Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
            Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                    LocalDateTime.now().plusYears(100), eventsIds);
            eventDtos = events.stream()
                    .map(event -> eventShortMapper.toDto(event,
                            countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                            statisticMap.get(event.getId())))
                    .collect(Collectors.toList());
        } else {
            events = null;
            eventDtos = null;
        }
        return Optional.of(newCompilationDto)
                .map(compilationDto -> newCompilationDtoMapper.toModel(compilationDto, events))
                .map(compilationRepository::save)
                .map(compilation -> compilationDtoMapper.toDto(compilation, eventDtos))
                .get();
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation) {
        Compilation compilation = getCompilationOrThrowException(compId);
        List<EventShortDto> eventDtos;
        List<Event> events = compilation.getEvents();

        if (updateCompilation.getTitle() != null) {
            compilation.setTitle(updateCompilation.getTitle());
        }
        if (updateCompilation.getPinned() != null) {
            compilation.setPinned(updateCompilation.getPinned());
        }
        if (updateCompilation.getEvents() != null) {
            events = eventRepository.findAllByIdIn(updateCompilation.getEvents());
            compilation.setEvents(events);
        }

        List<Long> eventsIds = getEventsId(events);

        Map<Long, Integer> countRequestsByEventId = getCountByEventId(eventsIds);
        Map<Long, Long> statisticMap = getStatisticMap(LocalDateTime.now().minusYears(100),
                LocalDateTime.now().plusYears(100), eventsIds);
        eventDtos = events.stream()
                .map(event -> eventShortMapper.toDto(event,
                        countRequestsByEventId.get(event.getId()) == null ? 0 : countRequestsByEventId.get(event.getId()),
                        statisticMap.get(event.getId())))
                .collect(Collectors.toList());


        return Optional.of(compilation)
                .map(compilationRepository::save)
                .map(comp -> compilationDtoMapper.toDto(comp, eventDtos))
                .get();
    }

    private Compilation getCompilationOrThrowException(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + compId + " not found"));
    }

    private static List<Long> getEventsId(List<Event> events) {
        return events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
    }

    private Map<Long, Integer> getCountByEventId(List<Long> eventIds) {
        List<Request> allByEventIdInAndStatus = requestRepository.findAllByEventIdInAndStatus(eventIds,
                RequestStatus.CONFIRMED);

        return allByEventIdInAndStatus.stream()
                .collect(Collectors.groupingBy(
                        request -> request.getEvent().getId(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue))
                );
    }

    private Map<Long, Long> getStatisticMap(LocalDateTime rangeStart, LocalDateTime rangeEnd, List<Long> eventsIds) {
        List<String> uris = eventsIds.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        ResponseEntity<Object> stats = statisticClient.getStats(rangeStart, rangeEnd, uris, false);
        List<ReadStatisticDto> statisticDtos;
        if (stats.getStatusCode().is2xxSuccessful()) {
            statisticDtos = objectMapper.convertValue(stats.getBody(), new TypeReference<>() {
            });
        } else {
            throw new RuntimeException(Objects.requireNonNull(stats.getBody()).toString());
        }

        return statisticDtos.stream()
                .collect(Collectors.toMap(
                        dto -> extractIdFromUri(dto.getUri()),
                        ReadStatisticDto::getHits)
                );
    }

    private Long extractIdFromUri(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[parts.length - 1]);
    }
}
