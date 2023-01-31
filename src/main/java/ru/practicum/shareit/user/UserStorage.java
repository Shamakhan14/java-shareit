package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {

    User createUser(UserDto userDto);
    List<User> getAllUsers();
    User updateUser(int userId, UserDto userDto);
    User getUserById(int userId);
    void deleteUserById(int userId);
}
