package ru.practicum.mainmodule.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.event.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Query("SELECT e " +
            "FROM Event e " +
            "WHERE (:text IS NULL OR lower(e.annotation) LIKE concat('%', lower(:text), '%') OR lower(e.description) LIKE concat('%', lower(:text), '%')) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate < :rangeEnd) " +
            "AND (:onlyAvailable IS false OR ((:onlyAvailable IS true AND e.participantLimit > (SELECT count (*) FROM Request r WHERE e.id = r.event.id))) " +
            "OR (e.participantLimit = 0)) " +
            "AND (:state IS e.state)")
    Page<Event> getAllEventsForPublic(
            @Nullable String text,
            @Nullable List<Long> categories,
            @Nullable Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            @Nullable Boolean onlyAvailable,
            EventState state,
            Pageable pageable
    );

    List<Event> findAllByIdIn(List<Long> eventIds);

    Optional<Event> findByIdAndState(Long eventId, EventState state);
}
