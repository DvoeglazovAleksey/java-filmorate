package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return inMemoryUserStorage.getUsers().values();
    }

    @GetMapping("/users/{id}")
    public User findUser(@PathVariable String id) {
        return userService.findUser(id);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> findFriend(@PathVariable String id) {
        return userService.findFriend(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> mutualFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.listMutualFriends(id, otherId);
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping("/users")
    public User put(@Valid @RequestBody User user) {
        return inMemoryUserStorage.put(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User putFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.putFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable String id, @PathVariable String friendId) {
        return userService.deleteFriend(id, friendId);
    }
}
