package ru.practicum.statisticservice.service;

import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    void saveHit(CreateStatisticDto statisticDto);
    List<ReadStatisticDto> getStatistics(String start, String end, List<String> uris, Boolean unique);
}
