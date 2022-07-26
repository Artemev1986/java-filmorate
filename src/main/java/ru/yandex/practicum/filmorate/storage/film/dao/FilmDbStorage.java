package ru.yandex.practicum.filmorate.storage.film.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component()
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, LikeStorage likeStorage, DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
        this.directorStorage = directorStorage;
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
        if (filmRows.next()) {
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
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id ", (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public List<Film> getPopularFilms(long count, Long year, Long genreId) {
        return jdbcTemplate.query("SELECT DISTINCT f.film_id AS f_id, " +
                        "f.name AS f_name, " +
                        "f.description AS f_description, " +
                        "f.duration AS f_duration, " +
                        "m.MPA_id AS m_id, " +
                        "m.name AS m_name, " +
                        "m.description AS m_description, " +
                        "f.release_date AS f_release_date," +
                        "l.cnt " +
                        "FROM films AS f " +
                        "LEFT JOIN" +
                        "(SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                        "GROUP BY  film_id ) AS l ON f.film_id = l.film_id " +
                        "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id " +
                        "LEFT JOIN FILM_GENRE AS fg ON f.FILM_ID = fg.FILM_ID " +
                        "LEFT JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                        "WHERE CASE WHEN ? IS NOT NULL AND ? IS NOT NULL THEN YEAR(RELEASE_DATE) = ? AND g.GENRE_ID = ? " +
                        " WHEN ? IS NOT NULL THEN YEAR(RELEASE_DATE) = ? " +
                        " WHEN ? IS NOT NULL THEN g.GENRE_ID = ? " +
                        " WHEN ? IS NULL AND ? IS NULL  THEN 1=1 END  " +
                        "ORDER BY cnt DESC LIMIT ?;"
                , (rs, rowNum) -> makeFilm(rs)
                , year, genreId, year, genreId, year, year, genreId, genreId, year, genreId, count);
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
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id ", (rs) -> {
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
        return jdbcTemplate.query("SELECT f.film_id AS f_id, " +
                "f.name AS f_name, " +
                "f.description AS f_description, " +
                "f.duration AS f_duration, " +
                "m.MPA_id AS m_id, " +
                "m.name AS m_name, " +
                "m.description AS m_description, " +
                "f.release_date AS f_release_date " +
                "FROM films AS f " +
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id " +
                "LEFT JOIN FILM_DIRECTORS AS fd on f.FILM_ID = fd.FILM_ID " +
                "WHERE fd.DIRECTOR_ID = ? " +
                "ORDER BY RELEASE_DATE ;", (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public List<Film> getDirectorFilmsSortLikes(long id) {
        return jdbcTemplate.query("SELECT f.film_id AS f_id, " +
                "                 f.name AS f_name, " +
                "                 f.description AS f_description, " +
                "                 f.duration AS f_duration, " +
                "                 m.MPA_id AS m_id, " +
                "                 m.name AS m_name, " +
                "                 m.description AS m_description, " +
                "                 f.release_date AS f_release_date, " +
                "                 FD.DIRECTOR_ID, " +
                "                 D.NAME " +
                "                 FROM films AS f " +
                "                 LEFT JOIN " +
                "                 (SELECT film_id, COUNT(user_id) as cnt FROM likes " +
                "                 group by film_id ) AS l ON f.film_id = l.film_id " +
                "                 LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id " +
                "                 left join FILM_DIRECTORS FD on f.FILM_ID = FD.FILM_ID " +
                "                 left join DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "                 where D.DIRECTOR_ID = ? " +
                "                 order by cnt  ;", (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public List<Film> searchForFilms(String query, String by) {

        final String searchFilmSql = "SELECT F.FILM_ID AS f_id, " +
                "F.NAME AS f_name, " +
                "F.DESCRIPTION AS f_description, " +
                "F.DURATION AS f_duration, " +
                "M.MPA_ID AS m_id, " +
                "M.NAME AS m_name, " +
                "M.DESCRIPTION AS m_description, " +
                "F.RELEASE_DATE AS f_release_date " +
                "FROM FILMS AS F " +
                "LEFT JOIN" +
                "(SELECT FILM_ID, COUNT(USER_ID) as cnt FROM LIKES " +
                "GROUP BY FILM_ID ) AS L ON F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN MPA AS M ON F.MPA_ID=M.MPA_ID " +
                "WHERE UPPER(F.NAME) LIKE UPPER ('%'||?||'%')";

        final String searchDirectorSql = "SELECT F.FILM_ID AS f_id, " +
                "F.NAME AS f_name, " +
                "F.DESCRIPTION AS f_description, " +
                "F.DURATION AS f_duration, " +
                "M.MPA_ID AS m_id, " +
                "M.NAME AS m_name, " +
                "M.DESCRIPTION AS m_description, " +
                "F.RELEASE_DATE AS f_release_date " +
                "FROM FILMS AS F " +
                "LEFT JOIN" +
                "(SELECT FILM_ID, COUNT(USER_ID) as cnt FROM LIKES " +
                "GROUP BY FILM_ID ) AS L ON F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN MPA AS M ON F.MPA_ID=M.MPA_ID " +
                "JOIN FILM_DIRECTORS AS DF ON f.FILM_ID = df.FILM_ID " +
                "JOIN DIRECTORS D ON DF.DIRECTOR_ID=D.DIRECTOR_ID " +
                "WHERE UPPER(D.NAME) LIKE UPPER('%'||?||'%')";

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
                "(SELECT film_id, user_id, COUNT(user_id) as cnt FROM likes " +
                "group by user_id ) AS l ON f.film_id = l.film_id " +
                "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id " +
                "WHERE l.user_id = ?" +
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
        film.setGenres(genreStorage.getGenresByFilmId(film.getId()));
        film.setDirectors(directorStorage.getDirectorByFilmId(film.getId()));
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("m_id"));
        mpa.setName(rs.getString("m_name"));
        mpa.setDescription(rs.getString("m_description"));
        film.setMpa(mpa);
        return film;
    }
}
