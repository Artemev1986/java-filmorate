package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDirectorsStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmDirectorsDbStorage implements FilmDirectorsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DirectorStorage directorStorage;

    public FilmDirectorsDbStorage(JdbcTemplate jdbcTemplate, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorStorage = directorStorage;
    }

    @Override
    public void addDirectorByFilm(Long film_id, Long director_id) {
        jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES ( ?, ? );",
                film_id, director_id);
    }

    @Override
    public Set<Director> getDirectorByFilmId(Long film_id) {
        List<Long> directorIds = jdbcTemplate.query("SELECT director_id" +
                        " FROM film_directors WHERE film_id = ? ORDER BY FILM_ID;",
                (rs, rowNum) -> rs.getLong("director_id"), film_id);
        if (directorIds.isEmpty()) {
            return new HashSet<>();
        } else {
            return directorIds.stream().map(id -> directorStorage.getDirectorById(id)
                    .orElse(new Director())).collect(Collectors.toSet());
        }
    }

    @Override
    public void deleteDirector(Long film_id) {
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?;", film_id);
    }
}
