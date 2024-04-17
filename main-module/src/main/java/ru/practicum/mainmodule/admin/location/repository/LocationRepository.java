package ru.practicum.mainmodule.admin.location.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainmodule.admin.location.dto.LocationFullDto;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.util.PageRequestFrom;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLon(Double lat, Double lon);

    @Query("SELECT l FROM Location l " +
            "WHERE (:nameIsNull = false OR l.name IS NULL)")
    Page<Location> getLocationForAdmin(
            Boolean nameIsNull,
            Integer from,
            Integer size,
            PageRequestFrom pageRequestFrom);
}