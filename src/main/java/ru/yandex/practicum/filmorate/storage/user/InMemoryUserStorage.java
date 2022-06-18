package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private long id;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        checkName(user);
        id++;
        user.setId(id);
        users.put(id, user);
        log.debug("Adding new user with id: {}", id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with id (" + id + ")  not found");
        }
        checkName(user);
        users.put(user.getId(), user);
        log.debug("User with id ({}) was updated", user.getId());
            return user;
    }

    @Override
    public List<User> findAll() {
        log.debug("Current user counts: {}", users.size());
        return List.copyOf(users.values());
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id (" + id + ") not found");
        }
        log.debug("User search by id: {}", id);
        return users.get(id);
    }

    @Override
    public void deleteUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id (" + id + ") not found");
        }
        users.remove(id);
        log.debug("User with id ({}) was deleted", id);
    }

    private void checkName(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
