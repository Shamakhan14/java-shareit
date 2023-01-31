package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(UserDto userDto) {
        return userStorage.createUser(userDto);
    }

    public List<User> getAllUsers () {
        return userStorage.getAllUsers();
    }

    public User updateUser(int userId, UserDto userDto) {
        return userStorage.updateUser(userId, userDto);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public void deleteUserById(int userId) {
        userStorage.deleteUserById(userId);
    }
}
