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
    public Collection<User> findAllUser() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public User findUser(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> findFriend(@PathVariable Long id) {
        return userService.findFriend(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> mutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.listMutualFriends(id, otherId);
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }
}
