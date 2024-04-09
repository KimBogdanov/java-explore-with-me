package ru.practicum.mainmodule.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.user.dto.UserShortDto;
import ru.practicum.mainmodule.user.model.User;

@Mapper(componentModel = "Spring")
public interface UserShortDtoMapper {

    User mapToUser(UserShortDto userShortDto);
}
