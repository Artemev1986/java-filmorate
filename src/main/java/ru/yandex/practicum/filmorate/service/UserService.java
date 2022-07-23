package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage,
                       LikeStorage likeStorage,
                       FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.likeStorage = likeStorage;
        this.filmStorage = filmStorage;
    }
    public List<User> findAll() {
        log.debug("Current user counts: {}", userStorage.findAll().size());
        return userStorage.findAll();
    }

    public User getUserById(long id) {
        log.debug("User search by id: {}", id);
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User with id (" + id + ") not found"));
    }

    public User addUser(User user) {
        checkName(user);
        userStorage.addUser(user);
        log.debug("Adding new user with id: {}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        checkName(user);
        getUserById(user.getId());
        log.debug("User with id ({}) was updated", user.getId());
        return userStorage.updateUser(user);
    }

    public void deleteUserById(long id) {
        getUserById(id);
        userStorage.deleteUserById(id);
        log.debug("User with id ({}) was deleted", id);
    }

    public void addFriend(long userId, long friendId) {
        getUserById(friendId); //Will throw an exception if there is no user with id
        getUserById(userId);
        userStorage.addFriend(userId, friendId);
        log.debug("{} added {} as a friend",
                getUserById(userId).getName(),
                getUserById(friendId).getName());
    }

    public void deleteFriend(long userId, long friendId) {
        getUserById(friendId); //Will throw an exception if there is no user with id
        getUserById(userId);
        userStorage.deleteFriend(userId, friendId);
        log.debug("{} deleted {} from friends",
                getUserById(userId).getName(),
                getUserById(friendId).getName());
    }

    public Set<User> getAllFriends(long userId) {
        Set<User> users = new HashSet<>();
        for (long id: getUserById(userId).getFriends()) {
            users.add(getUserById(id));
        }
        log.debug("Current friend counts: {}", users.size());
        return users;
    }

    public Set<User> getCommonFriends(long firstUserId, long secondUserId) {
        Set<User> commonFriendList = getAllFriends(firstUserId);
        Set<User> secondFriendList = getAllFriends(secondUserId);
        commonFriendList.retainAll(secondFriendList);
        log.debug("Current common friend counts: {}", commonFriendList.size());
        return commonFriendList;
    }

    public Set<Film> getRecommendations(long id) {
        getUserById(id); //Will throw an exception if there is no user with id
        Set<Film> recommendations = userStorage.getUserIdsForRecommendations(id).stream()
                .flatMap(userId -> likeStorage.getFilmIdsByUserId(userId).stream())
                .map(filmStorage::getFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        Set<Film> userFilms = likeStorage.getFilmIdsByUserId(id).stream()
                .map(filmStorage::getFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        recommendations.removeAll(userFilms);
        log.info("Recommendations for user {}: {}", id, recommendations);
        return recommendations;
    }

    private void checkName(User user) {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
