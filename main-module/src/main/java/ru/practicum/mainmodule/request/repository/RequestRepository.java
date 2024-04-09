package ru.practicum.mainmodule.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countAllByEvent_Id(Long id);
}
