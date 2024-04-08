package ru.practicum.mainmodule.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.event.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
