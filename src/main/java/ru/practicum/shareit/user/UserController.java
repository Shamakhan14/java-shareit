package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated(Create.class)
    public UserDto create(@RequestBody UserDto userDto) {
        if (isValidUserDto(userDto)) {
            UserDto response = userService.create(userDto);
            log.info("Пользователь {} успешно добавлен.", userDto.getName());
            return response;
        } else {
            throw new ValidationException("Неверно введены данные пользователя.");
        }
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Запрошен список пользователей.");
        return userService.getAll();
    }

    @PatchMapping("/{userId}")
    @Validated(Update.class)
    public UserDto update(@PathVariable int userId, @RequestBody UserDto userDto) {
        if (userDto.getEmail() != null && !isValidEmail(userDto.getEmail()))
            throw new RuntimeException("Неверный Email.");
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

    private boolean isValidUserDto(UserDto userDto) {
        if (userDto.getName() != null && userDto.getEmail() != null && isValidEmail(userDto.getEmail())) {
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }
}
