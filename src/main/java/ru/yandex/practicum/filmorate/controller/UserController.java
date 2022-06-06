package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private int id;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        checkName(user);
        id++;
        user.setId(id);
        users.put(id, user);
        log.debug("Adding new user with id: {}", id);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        try {
            if (user.getId() < 1) {
                throw new ValidationException("User id less then 1");
            }
            checkName(user);
            users.put(user.getId(), user);
            log.debug("User with id {} was updated", user.getId());
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    private void checkName(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
