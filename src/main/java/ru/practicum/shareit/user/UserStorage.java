package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    User create(User user);

    List<User> getAll();

    User update(User user);

    User getById(int userId);

    void deleteById(int userId);
}
