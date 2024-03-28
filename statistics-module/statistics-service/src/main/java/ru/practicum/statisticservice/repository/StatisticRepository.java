package ru.practicum.statisticservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.commondto.dto.ReadStatisticDto;
import ru.practicum.statisticservice.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {
    @Query(value = "select new ru.practicum.commondto.dto.ReadStatisticDto(s.app, s.uri, count(distinct s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.ip, s.uri " +
            "order by count(distinct s.ip) desc")
    List<ReadStatisticDto> findAllByTimestampBetweenStartAndEndUniqueIp(LocalDateTime start,
                                                                        LocalDateTime end);

    @Query(value = "select new ru.practicum.commondto.dto.ReadStatisticDto(s.ip, s.uri, count(s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.ip, s.uri " +
            "order by count(s.ip) desc ")
    List<ReadStatisticDto> findAllByTimestampBetweenStartAndEnd(LocalDateTime start,
                                                                LocalDateTime end);

    @Query(value = "select new ru.practicum.commondto.dto.ReadStatisticDto(s.ip, s.uri, count(distinct s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 and s.uri in ?3 " +
            "group by s.ip, s.uri " +
            "order by count(distinct s.ip) desc ")
    List<ReadStatisticDto> findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(LocalDateTime start,
                                                                                LocalDateTime end,
                                                                                List<String> uris);

    @Query(value = "select new ru.practicum.commondto.dto.ReadStatisticDto(s.ip, s.uri, count(s.ip)) " +
            "from Statistic as s " +
            "where s.timestamp between ?1 and ?2 and s.uri in ?3 " +
            "group by s.ip, s.uri " +
            "order by count(s.ip) desc ")
    List<ReadStatisticDto> findAllByTimestampBetweenStartAndEndWithUris(LocalDateTime start,
                                                                        LocalDateTime end,
                                                                        List<String> uris);
}
