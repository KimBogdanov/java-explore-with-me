package ru.practicum.mainmodule.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainmodule.event.dto.EventFullDto;
import ru.practicum.mainmodule.event.dto.UpdateEventAdminRequestDto;
import ru.practicum.mainmodule.event.service.EventService;
import ru.practicum.mainmodule.user.dto.UserDto;
import ru.practicum.mainmodule.user.dto.UserShortDto;
import ru.practicum.mainmodule.user.service.UserService;
import ru.practicum.mainmodule.category.dto.CategoryDto;
import ru.practicum.mainmodule.category.dto.CategoryShortDto;
import ru.practicum.mainmodule.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;

    @PostMapping("/users")
    public UserDto saveUser(@Valid @RequestBody UserShortDto userShortDto) {
        log.info("Save user name: {}, email: {}", userShortDto.getName(), userShortDto.getEmail());
        return userService.saveUser(userShortDto);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(required = false) List<Integer> ids,
                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Get user id in: {}, from: {}, to: {}", ids, from, size);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Delete user id: {}", userId);
        userService.deleteUser(userId);
    }

    @PostMapping("/categories")
    public CategoryDto saveCategory(@Valid @RequestBody CategoryShortDto categoryShortDto) {
        log.info("Save category name: {}", categoryShortDto.getName());
        return categoryService.save(categoryShortDto);
    }

    @PatchMapping("/categories/{categoryId}")
    public CategoryDto patchCategory(@Valid @RequestBody CategoryShortDto categoryShortDto,
                                     @PathVariable Long categoryId) {
        log.info("Patch category id: {}, name: {}", categoryId, categoryShortDto.getName());
        return categoryService.patchCategory(categoryId, categoryShortDto);
    }

    @DeleteMapping("/categories/{categoryId}")
    public void deleteCategory(@PathVariable Long categoryId) {
        log.info("Delete category id: {}", categoryId);
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("events/{eventId}")
    public EventFullDto adminPatchEvent(@RequestBody UpdateEventAdminRequestDto updateEventDto,
                                        @PathVariable Long eventId) {
        log.info("adminPatchEvent id: {}", eventId);
        return eventService.adminPatchEvent(eventId, updateEventDto);
    }
}
