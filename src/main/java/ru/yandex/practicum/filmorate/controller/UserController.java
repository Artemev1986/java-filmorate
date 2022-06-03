package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private int id;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping(value = "/users")
    public User addUser(@RequestBody User user) {
        try {
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
                throw new ValidationException("Email address is empty or doesn't contain @");
            }
            if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
                throw new ValidationException("Login is empty or contains a space");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Date of birth in the future");
            }
            if (user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            id++;
            user.setId(id);
            users.put(id, user);
            log.debug("Adding new user with id: {}", id);
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@RequestBody User user) {
        try {
            if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
                throw new ValidationException("Email address is empty or doesn't contain @");
            }
            if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
                throw new ValidationException("Login is empty or contains a space");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Date of birth in the future");
            }
            if (user.getId() < 1) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The HTTP Status will be Internal Server Error (CODE 500)\n");
            }
            if (user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.debug("User with id {} was updated", user.getId());
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return List.copyOf(users.values());
    }
}
