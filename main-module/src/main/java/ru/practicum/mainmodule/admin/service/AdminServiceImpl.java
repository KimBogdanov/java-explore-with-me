package ru.practicum.mainmodule.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainmodule.admin.dto.UserDto;
import ru.practicum.mainmodule.admin.dto.UserShortDto;
import ru.practicum.mainmodule.admin.mapper.UserDtoMapper;
import ru.practicum.mainmodule.admin.mapper.UserShortDtoMapper;
import ru.practicum.mainmodule.admin.model.User;
import ru.practicum.mainmodule.admin.repository.UserRepository;
import ru.practicum.mainmodule.exception.NotFoundException;
import ru.practicum.mainmodule.util.PageRequestFrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {
    private final UserShortDtoMapper userShortDtoMapper;
    private final UserDtoMapper userDtoMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto saveUser(UserShortDto userShortDto) {
        return Optional.of(userShortDto)
                .map(userShortDtoMapper::mapToUser)
                .map(userRepository::save)
                .map(userDtoMapper::userToUserDto)
                .get();
    }

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        Page<User> result;
        if (ids != null) {
            result = userRepository.findAllByIdIn(ids, new PageRequestFrom(from, size, null));
        } else {
            result = userRepository.findAll(new PageRequestFrom(from, size, null));
        }
        return result.getContent().stream()
                .map(userDtoMapper::userToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        userRepository.deleteById(userId);
    }
}
