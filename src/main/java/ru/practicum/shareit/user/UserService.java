package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        if (isDuplicate(userDto)) throw new DuplicateEmailException("Данный Email уже существует.");
        User user = userStorage.create(UserMapper.mapToNewUser(userDto));
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userStorage.getAll();
        return UserMapper.mapToUserDto(users);
    }

    public UserDto update(int userId, UserDto userDto) {
        if (!isValidId(userId)) throw new ValidationException("Неверный ID пользователя.");
        if (userDto.getEmail() != null && isDuplicate(userDto))
            throw new DuplicateEmailException("Данный Email уже существует.");
        User user = UserMapper.mapToNewUser(userDto);
        user.setId(userId);
        return UserMapper.mapToUserDto(userStorage.update(user));
    }

    public UserDto getById(int userId) {
        if (!isValidId(userId)) throw new ValidationException("Неверный ID пользователя.");
        return UserMapper.mapToUserDto(userStorage.getById(userId));
    }

    public void deleteById(int userId) {
        userStorage.deleteById(userId);
    }

    private boolean isDuplicate(UserDto userDto) {
        for (User user: userStorage.getAll()) {
            if (userDto.getEmail().equals(user.getEmail()) && userDto.getId() != user.getId()) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidId(int userId) {
        for (User user: userStorage.getAll()) {
            if (userId == user.getId()) {
                return true;
            }
        }
        return false;
    }
}
