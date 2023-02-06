package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private int ids = 0;
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(++ids);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) oldUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank()) oldUser.setEmail(user.getEmail());
        return oldUser;
    }

    @Override
    public User getById(int userId) {
        return users.get(userId);
    }

    @Override
    public void deleteById(int userId) {
        users.remove(userId);
    }
}
