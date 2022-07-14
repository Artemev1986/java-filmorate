package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update("INSERT INTO films" +
                        "(name," +
                        " description," +
                        " release_date," +
                        " duration," +
                        " MPA_id) " +
                        "VALUES (?, ?, ?, ?, ?);",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY film_id DESC LIMIT 1;");
        if(filmRows.next()) {
            film.setId(filmRows.getLong("film_id"));
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update("UPDATE films " +
                        "SET name = ?, " +
                        "description = ?, " +
                        "release_date = ?, " +
                        "duration = ?, " +
                        "MPA_id = ? " +
                        "WHERE film_id = ?;",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        return jdbcTemplate.query("SELECT f.film_id AS f_id, " +
                "f.name AS f_name, " +
                "f.description AS f_description, " +
                "f.duration AS f_duration, " +
                "m.MPA_id AS m_id, " +
                "m.name AS m_name, " +
                "m.description AS m_description, " +
                "f.release_date AS f_release_date " +
                "FROM films AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        return jdbcTemplate.query("SELECT f.film_id AS f_id, " +
                "f.name AS f_name, " +
                "f.description AS f_description, " +
                "f.duration AS f_duration, " +
                "m.MPA_id AS m_id, " +
                "m.name AS m_name, " +
                "m.description AS m_description, " +
                "f.release_date AS f_release_date " +
                "FROM films AS f " +
                "LEFT JOIN" +
                "(SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                "group by film_id ) AS l ON f.film_id = l.film_id " +
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id " +
                "order by cnt DESC LIMIT ?;", (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(jdbcTemplate.query("SELECT f.film_id AS f_id, " +
                "f.name AS f_name, " +
                "f.description AS f_description, " +
                "f.duration AS f_duration, " +
                "m.MPA_id AS m_id, " +
                "m.name AS m_name, " +
                "m.description AS m_description, " +
                "f.release_date AS f_release_date " +
                "FROM (SELECT * FROM films AS f WHERE film_id=?) AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;", (rs) -> {
            if (rs.next()) {
                return makeFilm(rs);
            }
            return null;
            }, id));
    }

    @Override
    public void deleteFilmById(long id) {
        jdbcTemplate.update("DELETE FROM films WHERE film_id = ?;", id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("f_id"));
        film.setName(rs.getString("f_name"));
        film.setDescription(rs.getString("f_description"));
        film.setReleaseDate(rs.getDate("f_release_date").toLocalDate());
        film.setDuration(rs.getInt("f_duration"));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("m_id"));
        mpa.setName(rs.getString("m_name"));
        mpa.setDescription(rs.getString("m_description"));
        film.setMpa(mpa);
        return film;
    }
}
