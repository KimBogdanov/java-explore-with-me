package ru.practicum.mainmodule.admin.service;

import ru.practicum.mainmodule.user.dto.UserDto;
import ru.practicum.mainmodule.user.dto.UserShortDto;

import java.util.List;

public interface AdminService {
    UserDto saveUser(UserShortDto userShortDto);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
