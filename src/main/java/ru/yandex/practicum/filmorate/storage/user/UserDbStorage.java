package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_USERS = "SELECT * FROM users ORDER BY user_id;";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?;";
    private static final String SQL_ADD_USER = "INSERT INTO users (name, email, login, birthday)" +
            " VALUES (?, ?, ?, ?);";
    private static final String SQL_UPDATE_USER =  "UPDATE users SET name = ?, email = ?, login = ?, " +
            "birthday = ? " +
            "WHERE user_id = ?;";
    private static final String SQL_ADD_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?);";
    private static final String SQL_CONFIRM_FRIEND = "UPDATE friends SET is_confirmed = ?" +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String SQL_DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = ?;";
    private static final String SQL_DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
    private static final String SQL_GET_FRIEND_BY_ID = "SELECT friend_id FROM friends WHERE user_id = ?;";
    private static final String SQL_GET_STATUS_FRIEND = "SELECT is_confirmed FROM friends WHERE user_id = ?" +
            " AND FRIEND_ID = ?";
    private static final String SQL_GET_LAST_ID = "SELECT * FROM users ORDER BY user_id DESC LIMIT 1;";


    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        jdbcTemplate.update(SQL_ADD_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday());
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SQL_GET_LAST_ID);
        if(userRows.next())
            user.setId(userRows.getLong("user_id"));
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query(SQL_GET_USERS, (rs, rowNum) -> makeUser(rs));
        users.forEach(user -> user.setFriends(getFriendsById(user.getId())));
        return users;
    }

    @Override
    public Optional<User> getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SQL_GET_USER_BY_ID, id);
        if(userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            user.setFriends(getFriendsById(user.getId()));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteUserById(long id) {
        jdbcTemplate.update(SQL_DELETE_USER_BY_ID, id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_ADD_FRIEND, userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_DELETE_FRIEND, userId, friendId);
    }

    @Override
    public void confirmFriend(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_CONFIRM_FRIEND, true, userId, friendId);
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
        List<Long> friends = jdbcTemplate.query(SQL_GET_FRIEND_BY_ID,
                (rs, rowNum) -> rs.getLong("friend_id"), id);
        return Set.copyOf(friends);
    }

}
