package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long film_id) {
        List<Integer> genreIds = jdbcTemplate.query("SELECT genre_id" +
                        " FROM film_genre WHERE film_id = ? ORDER BY genre_id;",
                (rs, rowNum) -> rs.getInt("genre_id"), film_id);
        if (genreIds.isEmpty()) {
            return new HashSet<>();
        } else {
            return genreIds.stream().map(id -> genreStorage.getGenreById(id)
                    .orElse(new Genre())).collect(Collectors.toSet());
        }
    }

    @Override
    public void addGenre(Long film_id, Integer genre_id) {
        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);", film_id, genre_id);
    }

    @Override
    public void deleteGenres(Long film_id) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?;", film_id);
    }

    @Override
    public int[] addGenres(Long film_id, List<Genre> genres) {
        return jdbcTemplate.batchUpdate(
                "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film_id);
                        ps.setInt(2, genres.get(i).getId());
                    }

                    public int getBatchSize() {
                        return genres.size();
                    }
                } );
    }
}
