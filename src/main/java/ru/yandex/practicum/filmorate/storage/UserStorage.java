package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> findUsers();

    User findUserById(long id);

    void addFriend(long userId, long friendId);

    void updateFriendship(long userId, long friendId, boolean status);

    void deleteFriend(long userId, long friendId);

    List<User> findAllFriendsById(long userId);

    List<User> findCommonFriends(long userId, long friendId);
}
