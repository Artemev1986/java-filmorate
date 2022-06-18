package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }
    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    public void addFriend(long userId, long friendId) {
        userStorage.getUserById(friendId); //Will throw an exception if there is no user with id
        userStorage.getUserById(userId).addFriend(friendId);
        log.debug("{} added {} as a friend",
                userStorage.getUserById(userId).getName(),
                userStorage.getUserById(friendId).getName());
    }

    public void deleteFriend(long userId, long friendId) {
        userStorage.getUserById(friendId); //Will throw an exception if there is no user with id
        userStorage.getUserById(userId).deleteFriend(friendId);
        log.debug("{} deleted {} from friends",
                userStorage.getUserById(userId).getName(),
                userStorage.getUserById(friendId).getName());
    }

    public Set<User> getAllFriends(long userId) {
        Set<User> users = new HashSet<>();
        for (long id: userStorage
                .getUserById(userId)
                .getFriends()) {
            users.add(userStorage.getUserById(id));
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
}
