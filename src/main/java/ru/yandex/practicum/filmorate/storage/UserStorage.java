package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User add(User user);

    User update(User user);

    long delete(String id);

    Map<Long, User> getUsers();
}
