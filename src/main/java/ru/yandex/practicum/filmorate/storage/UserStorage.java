package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    User create(User user);

    User put(User user);

    User delete(String id);

    Map<Long, User> getUsers();
}
