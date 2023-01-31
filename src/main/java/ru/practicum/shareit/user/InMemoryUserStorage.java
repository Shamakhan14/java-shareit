package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private int ids = 0;
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(UserDto userDto) {
        User user = new User(++ids, userDto.getName(), userDto.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(int userId, UserDto userDto) {
        User user = users.get(userId);
        if (userDto.getName() != null) user.setName(userDto.getName());
        if (userDto.getEmail() != null) user.setEmail(userDto.getEmail());
        return user;
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }
}
