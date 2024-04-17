package ru.practicum.mainmodule.admin.location.service;

import ru.practicum.mainmodule.admin.location.dto.LocationDto;
import ru.practicum.mainmodule.admin.location.model.Location;

public interface LocationService {
    Location save(LocationDto locationDto);
}
