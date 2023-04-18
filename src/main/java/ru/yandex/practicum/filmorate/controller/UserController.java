package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return userService.findAllUser();
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
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User put(@Valid @RequestBody User user) {
        return userService.putUser(user);
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
