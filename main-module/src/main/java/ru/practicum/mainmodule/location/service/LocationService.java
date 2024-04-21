package ru.practicum.mainmodule.location.service;

import ru.practicum.mainmodule.location.dto.LocationDto;
import ru.practicum.mainmodule.location.dto.LocationFullDto;
import ru.practicum.mainmodule.location.dto.NewLocationDto;
import ru.practicum.mainmodule.location.dto.UpdateLocationDto;
import ru.practicum.mainmodule.location.model.Location;

import java.util.List;

public interface LocationService {
    Location save(LocationDto locationDto);

    LocationFullDto saveLocation(NewLocationDto newLocationDto);

    List<LocationFullDto> getAllLocationForAdmin(Integer from, Integer size);

    LocationFullDto updateLocation(Long locationId, UpdateLocationDto updateLocationDto);

    List<LocationFullDto> getLocationsByCoordinatesAndRadius(
            Double lat, Double lon, float radius, Integer from, Integer size);

    LocationFullDto getLocationById(Long locationId);
}
