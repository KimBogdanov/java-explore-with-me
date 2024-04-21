package ru.practicum.mainmodule.location.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainmodule.location.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findLocationByLatAndLon(Double lat, Double lon);

    @Override
    Page<Location> findAll(Pageable pageable);

    @Query(value = "SELECT l FROM Location l WHERE distance(:lat, :lon, l.lat, l.lon) <= :radius")
    Page<Location> findLocationsInRadius(Double lat, Double lon, float radius, Pageable pageable);
}