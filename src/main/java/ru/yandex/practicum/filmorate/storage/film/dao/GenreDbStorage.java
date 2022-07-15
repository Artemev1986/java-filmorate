package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenre() {
        return jdbcTemplate.query("SELECT * FROM genres ORDER BY genre_id;", (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE genre_id = ?;", id);
        if(genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("genre_id"));
            genre.setName(genreRows.getString("name"));
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Set<Genre> getGenresByFilmId(Long film_id) {
        List<Integer> genreIds = jdbcTemplate.query("SELECT genre_id" +
                        " FROM FILM_GENRE WHERE film_id = ? ORDER BY genre_id;",
                (rs, rowNum) -> rs.getInt("genre_id"), film_id);
        if (genreIds.isEmpty()) {
            return new HashSet<>();
        } else {
            return genreIds.stream().map(id -> getGenreById(id)
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

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
