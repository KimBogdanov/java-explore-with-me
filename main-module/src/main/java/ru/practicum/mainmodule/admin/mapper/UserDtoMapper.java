package ru.practicum.mainmodule.admin.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.admin.dto.UserDto;
import ru.practicum.mainmodule.admin.model.User;

@Mapper(componentModel = "Spring")
public interface UserDtoMapper {
    UserDto userToUserDto(User user);
}
