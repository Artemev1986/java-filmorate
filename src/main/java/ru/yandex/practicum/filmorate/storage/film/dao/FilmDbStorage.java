package ru.yandex.practicum.filmorate.storage.film.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDirectorsStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component()
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final FilmDirectorsStorage filmDirectorsStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmGenreStorage filmGenreStorage,
                         LikeStorage likeStorage, FilmDirectorsStorage filmDirectorsStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.filmDirectorsStorage = filmDirectorsStorage;
    }

    @Override
    public Film addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((connection) -> {
            PreparedStatement ps =
                    connection.prepareStatement("INSERT INTO films" +
                            "(name," +
                            " description," +
                            " release_date," +
                            " duration," +
                            " MPA_id) " +
                            "VALUES (?, ?, ?, ?, ?);", new String[] {"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
            film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
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
        return jdbcTemplate.query("SELECT films.film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "films.duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "films.release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id ", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public List<Film> getPopularFilms(long count, Long year, Long genreId) {
        return jdbcTemplate.query("SELECT DISTINCT films.film_id AS f_id, " +
                        "films.name AS f_name, " +
                        "films.description AS f_description, " +
                        "films.duration AS f_duration, " +
                        "MPA.MPA_id AS m_id, " +
                        "MPA.name AS m_name, " +
                        "MPA.description AS m_description, " +
                        "films.release_date AS f_release_date," +
                        "likes_grouped.cnt " +
                        "FROM films " +
                        "LEFT JOIN" +
                        "(SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                        "GROUP BY  film_id ) AS likes_grouped ON films.film_id = likes_grouped.film_id " +
                        "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                        "LEFT JOIN film_genre ON films.FILM_ID = film_genre.film_id " +
                        "LEFT JOIN genres ON film_genre.genre_id = genres.genre_id " +
                        "WHERE CASE WHEN ? IS NOT NULL AND ? IS NOT NULL THEN YEAR(release_date) = ? " +
                        "AND genres.genre_id = ? " +
                        " WHEN ? IS NOT NULL THEN YEAR(release_date) = ? " +
                        " WHEN ? IS NOT NULL THEN genres.genre_id = ? " +
                        " WHEN ? IS NULL AND ? IS NULL  THEN 1=1 END  " +
                        "ORDER BY cnt DESC LIMIT ?;"
                , (rs, rowNum) -> makeFilm(rs)
                , year, genreId, year, genreId, year, year, genreId, genreId, year, genreId, count);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(jdbcTemplate.query("SELECT film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "WHERE film_id=? ", (rs) -> {
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

    @Override
    public List<Film> getDirectorFilmsSortYear(long id) {
        return jdbcTemplate.query("SELECT films.film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "films.duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "films.release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "LEFT JOIN film_directors on films.film_id = film_directors.film_id " +
                "WHERE film_directors.director_id = ? " +
                "ORDER BY release_date ;", (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public List<Film> getDirectorFilmsSortLikes(long id) {
        return jdbcTemplate.query("SELECT films.film_id AS f_id, " +
                "                 films.name AS f_name, " +
                "                 films.description AS f_description, " +
                "                 films.duration AS f_duration, " +
                "                 MPA.MPA_id AS m_id, " +
                "                 MPA.name AS m_name, " +
                "                 MPA.description AS m_description, " +
                "                 films.release_date AS f_release_date, " +
                "                 film_directors.director_id, " +
                "                 directors.name " +
                "                 FROM films " +
                "                 LEFT JOIN " +
                "                 (SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                "                 group by film_id ) AS likes_grouped ON films.film_id = likes_grouped.film_id " +
                "                 LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "                 left join film_directors on films.FILM_ID = film_directors.film_id " +
                "                 left join directors on film_directors.director_id = directors.director_id " +
                "                 where directors.director_id = ? " +
                "                 order by cnt  ;", (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public List<Film> searchForFilms(String query, String by) {

        final String searchFilmSql = "SELECT films.film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "films.duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "films.release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN" +
                "(SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                "GROUP BY film_id ) AS likes_grouped ON films.film_id = likes_grouped.film_id " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "WHERE UPPER(films.name) LIKE UPPER ('%'||?||'%')";

        final String searchDirectorSql = "SELECT films.film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "films.duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "films.release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN" +
                "(SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                "GROUP BY film_id ) AS likes_grouped ON films.film_id = likes_grouped.film_id " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "JOIN film_directors ON films.film_id = film_directors.film_id " +
                "JOIN directors ON film_directors.director_id=directors.director_id " +
                "WHERE UPPER(directors.name) LIKE UPPER('%'||?||'%')";

        final String searchForDirectorAndTitle = searchFilmSql + " UNION ALL " + searchDirectorSql;
        final String searchForTitleAndDirector = searchDirectorSql + " UNION ALL " + searchFilmSql;

        switch (by) {
            case "director":
                return jdbcTemplate.query(searchDirectorSql, (rs, rowNum) -> makeFilm(rs), query);
            case "title":
                return jdbcTemplate.query(searchFilmSql, (rs, rowNum) -> makeFilm(rs), query);
            case "director,title":
                return jdbcTemplate.query(searchForDirectorAndTitle, (rs, rowNum) -> makeFilm(rs), query, query);
            case "title,director":
                return jdbcTemplate.query(searchForTitleAndDirector, (rs, rowNum) -> makeFilm(rs), query, query);
            default:
                return getPopularFilms(10, null, null);
        }
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        List<Film> userFilms = getFilmsByUserId(userId);
        List<Film> friendFilms = getFilmsByUserId(friendId);

        return userFilms.stream()
                .filter(friendFilms::contains)
                .collect(Collectors.toList());
    }

    private List<Film> getFilmsByUserId(long id) {
        return jdbcTemplate.query("SELECT films.film_id AS f_id, " +
                "films.name AS f_name, " +
                "films.description AS f_description, " +
                "films.duration AS f_duration, " +
                "MPA.MPA_id AS m_id, " +
                "MPA.name AS m_name, " +
                "MPA.description AS m_description, " +
                "films.release_date AS f_release_date " +
                "FROM films " +
                "LEFT JOIN" +
                "(SELECT film_id, user_id, COUNT(user_id) as cnt FROM likes " +
                "group by user_id ) AS likes_grouped ON films.film_id = likes_grouped.film_id " +
                "LEFT JOIN MPA ON films.MPA_id=MPA.MPA_id " +
                "WHERE likes_grouped.user_id = ?" +
                "order by cnt DESC;", (rs, rowNum) -> makeFilm(rs), id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("f_id"));
        film.setName(rs.getString("f_name"));
        film.setDescription(rs.getString("f_description"));
        film.setReleaseDate(rs.getDate("f_release_date").toLocalDate());
        film.setDuration(rs.getInt("f_duration"));
        film.setLikes(likeStorage.getLikesByFilmId(film.getId()));
        film.setGenres(filmGenreStorage.getGenresByFilmId(film.getId()));
        film.setDirectors(filmDirectorsStorage.getDirectorByFilmId(film.getId()));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("m_id"));
        mpa.setName(rs.getString("m_name"));
        mpa.setDescription(rs.getString("m_description"));
        film.setMpa(mpa);
        return film;
    }
}
