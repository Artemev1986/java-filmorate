package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage{
    private long id;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        id++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
            return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }
}
