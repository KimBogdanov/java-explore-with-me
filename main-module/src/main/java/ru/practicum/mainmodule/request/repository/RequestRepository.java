package ru.practicum.mainmodule.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countAllByEvent_Id(Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);
}
