package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component()
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO directors" +
                    "(name) " +
                    "VALUES (?);", new String[] {"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    }

    @Override
    public void updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors " +
                        "SET name = ? " +
                        "WHERE director_id = ?;",
                director.getName(),
                director.getId());
    }

    @Override
    public List<Director> getAllDirector() {
        return jdbcTemplate.query("SELECT * FROM directors ORDER BY director_id;",
                (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?;", id);
        if (directorRows.next()) {
            Director director = new Director();
            director.setId(directorRows.getLong("director_id"));
            director.setName(directorRows.getString("name"));
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteDirectorById(long id) {
        jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?;", id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
