package ru.practicum.mainmodule.admin.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.admin.dto.UserShortDto;
import ru.practicum.mainmodule.admin.model.User;

@Mapper(componentModel = "Spring")
public interface UserShortDtoMapper {

    User mapToUser(UserShortDto userShortDto);
}
