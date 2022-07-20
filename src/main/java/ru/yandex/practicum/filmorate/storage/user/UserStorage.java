package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    List<User> findAll();

    Optional<User> getUserById(long id);

    void deleteUserById(long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void confirmFriend(Long userId, Long friendId);

    Optional<Boolean> isConfirmFriend(Long userId, Long friendId);

    List<Long> getUserIdsForRecommendations(Long userId);
}
