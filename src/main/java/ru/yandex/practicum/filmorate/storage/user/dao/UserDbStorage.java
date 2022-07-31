package ru.yandex.practicum.filmorate.storage.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users" +
                    "(name," +
                    " email," +
                    " login," +
                    " birthday)" +
                    " VALUES (?, ?, ?, ?);", new String[] {"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
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
        return jdbcTemplate.query("SELECT likes2.user_id AS similar_user_id " +
                        "FROM likes AS likes1 " +
                        "JOIN likes AS likes2 ON likes1.film_id = likes2.film_id " +
                        "AND likes1.user_id <> likes2.user_id " +
                        "WHERE likes1.user_id = ? " +
                        "GROUP BY similar_user_id " +
                        "HAVING count(*) = ( " +
                        "    SELECT count(*) AS max_matches_amount " +
                        "    FROM likes AS likes3 " +
                        "             JOIN likes AS likes4 ON likes3.film_id = likes4.film_id " +
                        "AND likes3.user_id <> likes4.user_id " +
                        "    WHERE likes3.user_id = ? " +
                        "    GROUP BY likes4.user_id " +
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
