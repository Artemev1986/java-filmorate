package ru.yandex.practicum.filmorate.storage.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.user.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addInFeed(long userId, String eventType, String operation, long entityId) {
        jdbcTemplate.update("INSERT INTO feed" +
                        "(time," +
                        "user_id," +
                        "event_type," +
                        "operation," +
                        "entity_id)" +
                        "VALUES (?, ?, ?, ?, ?);",
                new Timestamp(System.currentTimeMillis()),
                userId,
                eventType,
                operation,
                entityId);
    }

    @Override
    public List<Feed> getFeedByUserId(long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM feed \n" +
                        "WHERE user_id = ?\n" +
                        "ORDER BY time \n", (rs, rowNum) -> makeFeed(rs), userId);
    }

    private Feed makeFeed(ResultSet rs) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(rs.getLong("event_id"));
        feed.setTimestamp((rs.getTimestamp("time")).getTime());
        feed.setUserId(rs.getLong("user_id"));
        feed.setEventType(rs.getString("event_type"));
        feed.setOperation(rs.getString("operation"));
        feed.setEntityId(rs.getLong("entity_id"));
        return feed;
    }
}