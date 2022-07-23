package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping()
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping()
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@Valid @PathVariable long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public long deleteUserById(@Valid @PathVariable long id) {
        userService.deleteUserById(id);
        return id;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public long addFriend(@Valid @PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    //    userService.addFriend(friendId, id);
        return friendId;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public long deleteFriend(@Valid @PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
        userService.deleteFriend(friendId, id);
        return friendId;
    }

    @GetMapping("/{id}/friends")
    public Set<User> findAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> findCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendations(@PathVariable long id) {
        return userService.getRecommendations(id);
    }
}
