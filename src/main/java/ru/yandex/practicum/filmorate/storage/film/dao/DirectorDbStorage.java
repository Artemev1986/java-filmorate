package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID DESC LIMIT 1;");
        if (filmRows.next()) {
            director.setId(filmRows.getLong("director_id"));
        }
    }

    @Override
    public void addDirectorByFilm(Long film_id, Long director_id) {
        jdbcTemplate.update("INSERT INTO FILM_DIRECTORS (film_id, director_id) VALUES ( ?, ? );", film_id, director_id);
    }

    @Override
    public void updateDirector(Director director) {
        jdbcTemplate.update("UPDATE DIRECTORS " +
                        "SET name = ? " +
                        "WHERE DIRECTOR_ID = ?;",
                director.getName(),
                director.getId());
    }

    @Override
    public Set<Director> getDirectorByFilmId(Long film_id) {
        List<Long> directorIds = jdbcTemplate.query("SELECT director_id" +
                        " FROM FILM_DIRECTORS WHERE FILM_ID = ? ORDER BY FILM_ID;",
                (rs, rowNum) -> rs.getLong("director_id"), film_id);
        if (directorIds.isEmpty()) {
            return new HashSet<>();
        } else {
            return directorIds.stream().map(id -> getDirectorById(id)
                    .orElse(new Director())).collect(Collectors.toSet());
        }
    }


    @Override
    public List<Director> getAllDirector() {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS ORDER BY DIRECTOR_ID;", (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?;", id);
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
        jdbcTemplate.update("DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?;", id);
    }

    @Override
    public void deleteDirector(Long film_id) {
        jdbcTemplate.update("DELETE FROM FILM_DIRECTORS WHERE film_id = ?;", film_id);
    }


    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name"));
        return director;
    }
}
