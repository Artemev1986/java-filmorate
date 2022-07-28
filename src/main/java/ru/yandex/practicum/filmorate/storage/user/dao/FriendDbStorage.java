package ru.yandex.practicum.filmorate.storage.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component()
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id) VALUES (?, ?);", userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?;", userId, friendId);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        jdbcTemplate.update("UPDATE friends SET is_confirmed = ?" +
                "WHERE user_id = ? AND friend_id = ?;", true, userId, friendId);
    }

    @Override
    public Optional<Boolean> isConfirmFriend(Long userId, Long friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT is_confirmed FROM friends WHERE user_id = ?" +
                " AND friend_id = ?;", userId, friendId);
        if (userRows.next()) {
            return Optional.of(userRows.getBoolean("is_confirmed"));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<Long> getFriendsById(Long id) {
        List<Long> friends = jdbcTemplate.query("SELECT friend_id FROM friends WHERE user_id = ?;",
                (rs, rowNum) -> rs.getLong("friend_id"), id);
        return Set.copyOf(friends);
    }
}
