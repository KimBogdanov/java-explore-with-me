package ru.practicum.statisticservice.service;

import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    CreateStatisticDto saveHit(CreateStatisticDto statisticDto);
    List<ReadStatisticDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
