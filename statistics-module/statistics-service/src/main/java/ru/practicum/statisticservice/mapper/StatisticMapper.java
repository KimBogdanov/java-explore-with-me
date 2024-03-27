package ru.practicum.statisticservice.mapper;

import org.mapstruct.Mapper;
import ru.practicum.commondto.dto.CreateStatisticDto;
import ru.practicum.statisticservice.model.Statistic;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    Statistic toModel(CreateStatisticDto dto);
}
