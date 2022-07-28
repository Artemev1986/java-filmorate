package ru.yandex.practicum.filmorate.storage.user;

import java.util.Optional;
import java.util.Set;

public interface FriendStorage {
    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    void confirmFriend(Long userId, Long friendId);

    Optional<Boolean> isConfirmFriend(Long userId, Long friendId);

    Set<Long> getFriendsById(Long id);
}
