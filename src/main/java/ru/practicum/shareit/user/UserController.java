package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) {
        User user;
        if (isValidUserDto(userDto) && !isDuplicate(userDto.getEmail())) {
            user = userService.createUser(userDto);
            log.info("Пользователь " + userDto.getName() + " успешно добавлен.");
        } else {
            throw new RuntimeException("Пользователь с данным email уже существует.");
        }
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрошен список пользователей.");
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody UserDto userDto) {
        if (isValidId(userId)) {
            if (userDto.getEmail() != null) {
                if (!isValidEmail(userDto.getEmail()) || isDuplicate(userDto.getEmail())) {
                    throw new RuntimeException("Неверный Email.");
                }
            }
            User user = userService.updateUser(userId, userDto);
            log.info("Пользователь успешно обновлен.");
            return user;
        } else {
            throw new ValidationException("Ошибка при обновлении данных пользователя.");
        }
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable int userId) {
        if (isValidId(userId)) {
            User user = userService.getUserById(userId);
            log.info("Запрошен данные пользователя " + userService.getUserById(userId).getName() + ".");
            return user;
        } else {
            throw new ValidationException("Ошибка при получении данных пользователя.");
        }
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable int userId) {
        if (isValidId(userId)) {
            userService.deleteUserById(userId);
            log.info("Данные пользователя удалены.");
        }else {
            throw new ValidationException("Пользователь не найден.");
        }
    }

    private boolean isDuplicate(String email) {
        for (User user: getAllUsers()) {
            if (email.equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidId(int userId) {
        for (User user: getAllUsers()) {
            if (userId == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidUserDto(UserDto userDto) {
        if (userDto.getName() != null && userDto.getEmail() != null && isValidEmail(userDto.getEmail())) {
            return true;
        }
        throw new ValidationException("Неверные данные пользователя.");
    }

    private boolean isValidEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(regexPattern).matcher(email).matches();
    }
}
