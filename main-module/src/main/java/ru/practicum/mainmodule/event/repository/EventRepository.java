package ru.practicum.mainmodule.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findAllByEventDateBetween(LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndCategoryIdIn(LocalDateTime rangeStart,
                                                         LocalDateTime rangeEnd,
                                                         List<Long> categoryIds,
                                                         Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndStateIn(LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    List<EventState> state,
                                                    Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndCategoryIdInAndStateIn(LocalDateTime rangeStart,
                                                                   LocalDateTime rangeEnd,
                                                                   List<Long> categoryIds,
                                                                   List<EventState> state,
                                                                   Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndInitiatorIdIn(LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd,
                                                          List<Long> initiatorId,
                                                          Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndCategoryIdInAndInitiatorIdIn(LocalDateTime rangeStart,
                                                                         LocalDateTime rangeEnd,
                                                                         List<Long> categoryIds,
                                                                         List<Long> initiatorId,
                                                                         Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndStateInAndInitiatorIdIn(LocalDateTime rangeStart,
                                                                    LocalDateTime rangeEnd,
                                                                    List<EventState> state,
                                                                    List<Long> initiatorId,
                                                                    Pageable pageable);

    Page<Event> findAllByEventDateBetweenAndCategoryIdInAndStateInAndInitiatorIdIn(LocalDateTime rangeStart,
                                                                                   LocalDateTime rangeEnd,
                                                                                   List<Long> categoryIds,
                                                                                   List<EventState> state,
                                                                                   List<Long> initiatorId,
                                                                                   Pageable pageable);
    Page<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);
}
