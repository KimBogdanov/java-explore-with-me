package ru.practicum.mainmodule.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdInAndEventId(List<Long> ids, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long requestId, RequestStatus status);
}
