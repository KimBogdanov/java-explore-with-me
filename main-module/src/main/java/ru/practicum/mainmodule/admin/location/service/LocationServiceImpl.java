package ru.practicum.mainmodule.admin.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.admin.location.dto.LocationDto;
import ru.practicum.mainmodule.admin.location.mapper.LocationDtoMapper;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.repository.LocationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationDtoMapper locationDtoMapper;

    @Override
    @Transactional
    public Location save(LocationDto locationDto) {
        Location location = locationRepository.findLocationByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location == null) {
            return locationRepository.save(locationDtoMapper.toModel(locationDto));
        }
        return location;
    }
}
