package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component()
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendStorage friendStorage;

    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendStorage friendStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendStorage = friendStorage;
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
        if (userRows.next())
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
        users.forEach(user -> user.setFriends(friendStorage.getFriendsById(user.getId())));
        return users;
    }

    @Override
    public Optional<User> getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?;", id);
        if (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
            user.setFriends(friendStorage.getFriendsById(user.getId()));
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
    public List<Long> getUserIdsForRecommendations(Long userId) {
        return jdbcTemplate.query("SELECT l2.user_id AS similar_user_id " +
                        "FROM likes AS l1 " +
                        "JOIN likes AS l2 ON l1.film_id = l2.film_id AND l1.user_id <> l2.user_id " +
                        "WHERE l1.user_id = ? " +
                        "GROUP BY similar_user_id " +
                        "HAVING count(*) = ( " +
                        "    SELECT count(*) AS max_matches_amount " +
                        "    FROM likes AS l3 " +
                        "             JOIN likes AS l4 ON l3.film_id = l4.film_id AND l3.user_id <> l4.user_id " +
                        "    WHERE l3.user_id = ? " +
                        "    GROUP BY l4.user_id " +
                        "    ORDER BY count(*) DESC " +
                        "    LIMIT 1 " +
                        "    );",
                (rs, rowNum) -> rs.getLong("similar_user_id"), userId, userId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriends(friendStorage.getFriendsById(user.getId()));
        return user;
    }
}
