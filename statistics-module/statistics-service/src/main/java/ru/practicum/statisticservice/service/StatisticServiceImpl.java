package ru.practicum.statisticservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.statisticservice.mapper.StatisticMapper;
import ru.practicum.statisticservice.repository.StatisticRepository;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final static DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    @Override
    public void saveHit(CreateStatisticDto statisticDto) {
        Optional.of(statisticDto)
                .map(statisticMapper::toModel)
                .map(statisticRepository::save);
    }

    @Override
    public List<ReadStatisticDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = decode(start);
        LocalDateTime endTime = decode(end);
        checkDateAndThrowException(startTime, endTime);

        if (unique) {
            if (uris == null) {
                return statisticRepository.findAllByTimestampBetweenStartAndEndUniqueIp(startTime, endTime);
            } else {
                return statisticRepository.findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(startTime, endTime, uris);
            }
        } else {
            if (uris == null) {
                return statisticRepository.findAllByTimestampBetweenStartAndEnd(startTime, endTime);
            } else {
                return statisticRepository.findAllByTimestampBetweenStartAndEndWithUris(startTime, endTime, uris);
            }
        }
    }

    private void checkDateAndThrowException(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start after end");
        }
    }

    private LocalDateTime decode(String dateTime) {
        return LocalDateTime.parse(URLDecoder.decode(dateTime, StandardCharsets.UTF_8), PATTERN);
    }
}
