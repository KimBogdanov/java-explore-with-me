package ru.practicum.mainmodule.admin.location.service;

import ru.practicum.mainmodule.admin.location.dto.LocationDto;
import ru.practicum.mainmodule.admin.location.dto.LocationFullDto;
import ru.practicum.mainmodule.admin.location.dto.NewLocationDto;
import ru.practicum.mainmodule.admin.location.model.Location;

import java.util.List;

public interface LocationService {
    Location save(LocationDto locationDto);

    LocationFullDto saveLocation(NewLocationDto newLocationDto);

    List<LocationFullDto> getAllLocationForAdmin(Boolean nameIsNull, Integer from, Integer size);
}
