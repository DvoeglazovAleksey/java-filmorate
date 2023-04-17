package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUser(String id) {
        if (!(userStorage.getUsers().containsKey(Long.parseLong(id)))) {
            throw new NullPointerException("Пользователь не найден с id " + id);
        }
        return userStorage.getUsers().get(Long.parseLong(id));
    }

    public User putFriend(String id, String friendId) {
        Long idUser = Long.parseLong(id);
        Long idFriend = Long.parseLong(friendId);
        if ((!(userStorage.getUsers().containsKey(idUser)) || !(userStorage.getUsers().containsKey(idFriend)))) {
            throw new NullPointerException("Пользователь не найден с id или friendId " + id + "," + friendId);
        } else {
            userStorage.getUsers().get(idUser).getFriends().add(idFriend);
            userStorage.getUsers().get(idFriend).getFriends().add(idUser);
        }
        return userStorage.getUsers().get(idUser);
    }

    public Long deleteFriend(String id, String friendId) {
        Long idUser = Long.parseLong(id);
        Long idFriend = Long.parseLong(friendId);
        if ((!(userStorage.getUsers().containsKey(idUser)) || !(userStorage.getUsers().containsKey(idFriend)))) {
            throw new NullPointerException("Пользователь не найден с id или friendId " + id + "," + friendId);
        } else {
            userStorage.getUsers().get(idUser).getFriends().remove(idFriend);
            userStorage.getUsers().get(idFriend).getFriends().remove(idUser);
        }
        return idUser;
    }

    public Collection<User> findFriend(String id) {
        if (!(userStorage.getUsers().containsKey(Long.parseLong(id)))) {
            throw new NullPointerException("Пользователь не найден с id " + id);
        } else {
            List<User> usersFriend = new ArrayList<>();
            for (Long aLong : userStorage.getUsers().get(Long.parseLong(id)).getFriends()) {
                usersFriend.add(userStorage.getUsers().get(aLong));
            }
            return usersFriend;
        }
    }

    public Collection<User> listMutualFriends(String id, String friendId) {
        Long idUser = Long.parseLong(id);
        Long idFriend = Long.parseLong(friendId);
        if (!(userStorage.getUsers().containsKey(idUser)) || !(userStorage.getUsers().containsKey(idFriend))) {
            throw new NullPointerException("Пользователь не найден с id или friendId " + id + "," + friendId);
        } else {
            Collection<User> mutualFriends = new ArrayList<>();
            for (Long friend : userStorage.getUsers().get(idUser).getFriends()) {
                if (userStorage.getUsers().get(idFriend).getFriends().contains(friend)) {
                    mutualFriends.add(userStorage.getUsers().get(friend));
                }
            }
            return mutualFriends;
        }
    }
}
