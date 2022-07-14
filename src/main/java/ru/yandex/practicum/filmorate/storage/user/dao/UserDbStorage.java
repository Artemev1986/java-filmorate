package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component()
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        jdbcTemplate.update("INSERT INTO users (name, email, login, birthday)" +
                        " VALUES (?, ?, ?, ?);",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users ORDER BY user_id DESC LIMIT 1;");
        if(userRows.next())
            user.setId(userRows.getLong("user_id"));
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET name = ?, email = ?, login = ?, " +
                        "birthday = ? " +
                        "WHERE user_id = ?;",
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY user_id;",
                (rs, rowNum) -> makeUser(rs));
        users.forEach(user -> user.setFriends(getFriendsById(user.getId())));
        return users;
    }

    @Override
    public Optional<User> getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?;", id);
        if(userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
            user.setFriends(getFriendsById(user.getId()));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUserById(long id) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?;", id);
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
        if(userRows.next()) {
            return Optional.of(userRows.getBoolean("is_confirmed"));
        } else {
            return Optional.empty();
        }
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriends(getFriendsById(user.getId()));
        return user;
    }

    private Set<Long> getFriendsById(Long id) {
        List<Long> friends = jdbcTemplate.query("SELECT friend_id FROM friends WHERE user_id = ?;",
                (rs, rowNum) -> rs.getLong("friend_id"), id);
        return Set.copyOf(friends);
    }

}
