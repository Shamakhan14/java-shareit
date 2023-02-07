package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto userDto) {
        UserDto response = userService.create(userDto);
        log.info("Пользователь {} успешно добавлен.", userDto.getName());
        return response;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрошен список пользователей.");
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable int userId, @RequestBody @Validated(Update.class) UserDto userDto) {
        UserDto response = userService.update(userId, userDto);
        log.info("Пользователь успешно обновлен.");
        return response;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        UserDto response = userService.getById(userId);
        log.info("Запрошен данные пользователя {}.", response.getName());
        return response;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        userService.deleteById(userId);
        log.info("Данные пользователя удалены.");
    }
}
