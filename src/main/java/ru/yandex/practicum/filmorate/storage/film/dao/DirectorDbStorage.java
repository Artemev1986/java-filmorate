package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component()
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addDirector(Director director) {
        jdbcTemplate.update("INSERT INTO directors" +
                        "(name) " +
                        "VALUES (?);",
                director.getName());
        SqlRowSet filmRows = jdbcTemplate
                .queryForRowSet("SELECT * FROM directors ORDER BY director_id DESC LIMIT 1;");
        if (filmRows.next()) {
            director.setId(filmRows.getLong("director_id"));
        }
    }

    @Override
    public void updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors " +
                        "SET name = ? " +
                        "WHERE DIRECTOR_ID = ?;",
                director.getName(),
                director.getId());
    }

    @Override
    public List<Director> getAllDirector() {
        return jdbcTemplate.query("SELECT * FROM directors ORDER BY DIRECTOR_ID;",
                (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE DIRECTOR_ID = ?;", id);
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
