package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long idUser = 1L;

    @Override
    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new ValidationException("Birthday, дата рождения не может быть в будущем.");
        } else {
            user.setId(idUser++);
            users.put(user.getId(), user);
            log.info("Добавлен пользователь {}", user.getName());
        }
        return user;
    }

    @Override
    public User put(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Обновлен пользователь {}", user.getName());
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения {}", user.getBirthday());
            throw new ValidationException("Birthday, дата рождения не может быть в будущем.");
        } else if (!users.containsKey(user.getId())) {
            log.error("Нельзя обновить: пользователя с id {} нет в базе данных", user.getId());
            throw new NullPointerException("Id, пользователя нет в базе данных.");
        } else {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь {}", user.getName());
        }
        return user;
    }

    @Override
    public User delete(String id) {
        if (!(users.containsKey(Long.parseLong(id)))) {
            throw new NullPointerException("User c " + id + " нет в базе, для удаления");
        } else {
            return users.remove(Long.parseLong(id));
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }
}
