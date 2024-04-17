package ru.practicum.mainmodule.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainmodule.request.model.Request;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countAllByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdInAndEventId(List<Long> ids, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long requestId, RequestStatus status);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event.id = ?1 AND r.status = ?2")
    Integer countAllRequestByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);
}
