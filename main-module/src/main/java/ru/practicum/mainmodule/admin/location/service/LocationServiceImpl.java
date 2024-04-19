package ru.practicum.mainmodule.admin.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.admin.location.dto.LocationDto;
import ru.practicum.mainmodule.admin.location.dto.LocationFullDto;
import ru.practicum.mainmodule.admin.location.dto.NewLocationDto;
import ru.practicum.mainmodule.admin.location.dto.UpdateLocationDto;
import ru.practicum.mainmodule.admin.location.mapper.LocationDtoMapper;
import ru.practicum.mainmodule.admin.location.mapper.LocationFullDtoMapper;
import ru.practicum.mainmodule.admin.location.mapper.NewLocationDtoMapper;
import ru.practicum.mainmodule.admin.location.model.Location;
import ru.practicum.mainmodule.admin.location.repository.LocationRepository;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.util.PageRequestFrom;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationDtoMapper locationDtoMapper;
    private final NewLocationDtoMapper newLocationDtoMapper;
    private final LocationFullDtoMapper locationFullDtoMapper;

    @Override
    @Transactional
    public Location save(LocationDto locationDto) {
        Location location = locationRepository.findLocationByLatAndLon(locationDto.getLat(), locationDto.getLon());
        if (location == null) {
            return locationRepository.save(locationDtoMapper.toModel(locationDto));
        }
        return location;
    }

    @Override
    @Transactional
    public LocationFullDto saveLocation(NewLocationDto newLocationDto) {
        return Optional.of(newLocationDto)
                .map(newLocationDtoMapper::toModel)
                .map(locationRepository::save)
                .map(locationFullDtoMapper::toDto)
                .get();
    }

    @Override
    public List<LocationFullDto> getAllLocationForAdmin(Boolean nameIsNull, Integer from, Integer size) {
        return locationRepository.getLocationForAdmin(
                        nameIsNull,
                        new PageRequestFrom(from, size, null)).stream()
                .map(locationFullDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LocationFullDto updateLocation(Long locationId, UpdateLocationDto updateLocationDto) {
        Location location = getLocationOrThrowNotFoundException(locationId);
        if (updateLocationDto.getName() != null) {
            location.setName(updateLocationDto.getName());
        }
        if (updateLocationDto.getDescription() != null) {
            location.setDescription(updateLocationDto.getDescription());
        }
        return locationFullDtoMapper.toDto(location);
    }

    @Override
    public List<LocationFullDto> getLocationsByCoordinatesAndRadius(
            Double lat, Double lon, float radius, Integer from, Integer size) {
        return locationRepository.findLocationsInRadius(lat, lon, radius, new PageRequestFrom(from, size, null))
                .stream()
                .map(locationFullDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private Location getLocationOrThrowNotFoundException(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException(String.format("Location with id=%d was not found", locationId)));
    }
}
