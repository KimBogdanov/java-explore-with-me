package ru.practicum.mainmodule.admin.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainmodule.admin.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLot(Double lat, Double lot);
}