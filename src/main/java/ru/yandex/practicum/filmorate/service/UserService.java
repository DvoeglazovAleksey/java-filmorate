package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAllUsers() {
        log.info("На данный момент сохранено пользователей: {}", userStorage.findUsers().size());
        List<User> allUsers = userStorage.findUsers();
        for (User user : allUsers) {
            Set<Long> usersFriends = user.getFriends();
            usersFriends.addAll(userStorage.findAllFriendsById(user.getId()).stream().map(User::getId)
                    .collect(Collectors.toSet()));
        }
        return allUsers;
    }

    public User findUserById(long id) {
        validateUserById(id);
        log.info("Нашли пользователя с id = {}", id);
        User user = userStorage.findUserById(id);
        Set<Long> usersFriends = user.getFriends();
        usersFriends.addAll(userStorage.findAllFriendsById(user.getId()).stream().map(User::getId)
                .collect(Collectors.toSet()));
        return user;
    }

    public User addUser(User user) {
        validateBeforeAdd(user);
        Set<Long> usersFriends = user.getFriends();
        for (Long friendId : usersFriends) {
            if (!userStorage.findUsers().contains(userStorage.findUserById(friendId))) {
                log.error("Пользователя с id = {} еще не существует", friendId);
                usersFriends.remove(friendId);
                throw new ValidationException(format("Пользователя с id = %s еще не существует", friendId));
            }
        }
        User createdUser = userStorage.addUser(user);
        Set<Long> friends = user.getFriends();
        for (Long friendId : friends) {
            addFriend(createdUser.getId(), friendId);
        }
        log.info("Добавили пользователя: {}", createdUser);
        return createdUser;
    }

    public User updateUser(User user) {
        try {
            validateUserById(user.getId());
            Set<Long> userFriends = user.getFriends();
            for (Long friendsId : userFriends) {
                validateUserById(user.getId());
                addFriend(user.getId(), friendsId);
            }
        } catch (EmptyResultDataAccessException e) {
            log.error("Пользователя с id=" + user.getId() + " не существует");
            throw new ValidationException(format("Пользователя с id = %s еще не существует", user.getId()));
        }
        log.info("Обновили пользователя с id = {}", user.getId());
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.error("Себя в друзья к себе добавить нельзя");
            throw new ValidationException("Себя в друзья к себе добавить нельзя, к сожаленью :)");
        }
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        Set<Long> usersFriends = user.getFriends();
        Set<Long> friendsFriends = friend.getFriends();
        boolean isUserHasFriend = usersFriends.contains(friendId);
        boolean isFriendHasUser = friendsFriends.contains(userId);
        if (!isUserHasFriend && !isFriendHasUser) {
            userStorage.addFriend(userId, friendId);
            usersFriends.add(friendId);
            log.info("Пользователь id = {} добавил в друзья пользователя id = {}", userId, friendId);
        } else if (!isUserHasFriend) {
            userStorage.addFriend(userId, friendId);
            userStorage.updateFriendship(userId, friendId, true);
            userStorage.updateFriendship(friendId, userId, true);
            log.info("Пользователь id = {} подтвердил дружбу с пользователем id = {}", userId, friendId);
            usersFriends.add(friendId);
        } else {
            log.info("Пользователь id = {} уже в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s уже в друзьях у пользователя id = %s",
                    friendId, userId));
        }
    }

    public void deleteFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        Set<Long> usersFriends = user.getFriends();
        Set<Long> friendsFriends = friend.getFriends();
        if (!usersFriends.contains(friendId)) {
            log.error("Пользователь с id = {} не в друзьях у пользователя id = {}", friendId, userId);
            throw new ValidationException(format("Пользователь id = %s не в друзьях у пользователя id = %s",
                    friendId, userId));
        } else if (!friendsFriends.contains(userId)) {
            userStorage.deleteFriend(userId, friendId);
            log.info("Пользователь с id = {} удалил из друзей пользователя id = {}", userId, friendId);
        } else {
            userStorage.deleteFriend(userId, friendId);
            userStorage.updateFriendship(friendId, userId, false);
            log.info("Пользователь с id = {} удалил из друзей пользователя id = {}, статус дружбы обновлен",
                    userId, friendId);
        }
    }

    public Collection<User> findFriend(long userId) {
        if (userStorage.findUserById(userId) == null) {
            log.error("Пользователя с id={} не существует", userId);
            throw new ValidationException(format("Пользователя с id = %s не существует", userId));
        }
        log.info("Получили список друзей пользователя id={}", userId);
        return userStorage.findAllFriendsById(userId);
    }

    public Collection<User> listMutualFriends(long userId, long friendId) {
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        log.info("Получили список общих друзей пользователей с id={} и id={}", userId, friendId);
        return userStorage.findCommonFriends(userId, friendId);
    }

    private void validateUserById(long userId) {
        try {
            if (userStorage.findUserById(userId) == null) {
                log.error("Пользователя с id={} не существует", userId);
                throw new NullPointerException(format("Пользователя с id= %s нет в базе", userId));
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NullPointerException(format("Пользователя с id= %s нет в базе", userId));
        }
    }

    private void validateBeforeAdd(User user) {
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелы");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        List<User> usersFromDB = userStorage.findUsers();
        for (User user1 : usersFromDB) {
            if (user1.getEmail().equals(user.getEmail())) {
                log.error("Пользователь с email = {} уже существует", user.getEmail());
                throw new ValidationException(format("Пользователь с email = %s уже существует", user.getEmail()));
            }
            if (user1.getLogin().equals(user.getLogin())) {
                log.error("Пользователь с login = {} уже существует", user.getLogin());
                throw new ValidationException(format("Пользователь с login = %s уже существует", user.getLogin()));
            }
        }
    }
}
