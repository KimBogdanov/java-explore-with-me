package ru.practicum.mainmodule.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainmodule.user.dto.UserDto;
import ru.practicum.mainmodule.admin.model.User;

@Mapper(componentModel = "Spring")
public interface UserDtoMapper {
    UserDto userToUserDto(User user);
}
