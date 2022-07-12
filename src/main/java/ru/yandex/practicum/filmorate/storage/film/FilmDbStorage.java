package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage, GenreStorage, MPAStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_GET_FILM_BY_ID = "SELECT f.film_id AS f_id, " +
            "f.name AS f_name, " +
            "f.description AS f_description, " +
            "f.duration AS f_duration, " +
            "m.MPA_id AS m_id, " +
            "m.name AS m_name, " +
            "m.description AS m_description, " +
            "f.release_date AS f_release_date " +
            "FROM (SELECT * FROM films AS f WHERE film_id=?) AS f " +
            "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;";

    private static final String SQL_GET_ALL_FILMS = "SELECT f.film_id AS f_id, " +
            "f.name AS f_name, " +
            "f.description AS f_description, " +
            "f.duration AS f_duration, " +
            "m.MPA_id AS m_id, " +
            "m.name AS m_name, " +
            "m.description AS m_description, " +
            "f.release_date AS f_release_date " +
            "FROM films AS f " +
            "LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;";

    private static final String SQL_ADD_FILM = "INSERT INTO films (name," +
            " description," +
            " release_date," +
            " duration," +
            " MPA_id) " +
            "VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_UPDATE_FILM = "UPDATE films " +
            "SET name = ?, " +
            "description = ?, " +
            "release_date = ?, " +
            "duration = ?, " +
            "MPA_id = ? " +
            "WHERE film_id = ?;";
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?;";
    private static final String SQL_ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?);";
    private static final String SQL_DELETE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?;";
    private static final String SQL_SELECT_LIKE_BY_FILM_ID = "SELECT user_id FROM likes WHERE film_id = ?;";
    private static final String SQL_GET_GENRE_BY_FILM_ID = "SELECT genre_id" +
            " FROM FILM_GENRE WHERE film_id = ? ORDER BY genre_id;";
    private static final String SQL_ADD_GENRE = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private static final String SQL_DELETE_GENRE = "DELETE FROM film_genre WHERE film_id = ?;";

    private static final String SQL_GET_ALL_MPA = "SELECT * FROM MPA ORDER BY MPA_id;";

    private static final String SQL_GET_ALL_GENRE = "SELECT * FROM genres ORDER BY genre_id;";

    private static final String SQL_GET_POPULAR_FILMS = "SELECT f.film_id AS f_id, " +
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
            "order by cnt DESC LIMIT ?;";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update(SQL_ADD_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY film_id DESC LIMIT 1");
        if(filmRows.next())
            film.setId(filmRows.getLong("film_id"));
        if (film.getGenres() != null && !film.getGenres().isEmpty())
            film.getGenres().forEach(genre -> addGenre(film.getId(), genre.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
        deleteGenres(film.getId());
        if (!film.getGenres().isEmpty())
            film.getGenres().forEach(genre -> addGenre(film.getId(), genre.getId()));
        }
        return getFilmById(film.getId()).orElse(film);
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SQL_GET_ALL_FILMS, (rs, rowNum) -> makeFilm(rs));
        films.forEach(f -> {
            f.setLikes(getLikesByFilmId(f.getId()));
            f.setGenres(getGenresByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public List<Film> getPopularFilms(long count) {
        List<Film> films = jdbcTemplate.query(SQL_GET_POPULAR_FILMS, (rs, rowNum) -> makeFilm(rs), count);
        films.forEach(f -> {
            f.setLikes(getLikesByFilmId(f.getId()));
            f.setGenres(getGenresByFilmId(f.getId()));
        });
        return films;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        Optional<Film> film = Optional.ofNullable(jdbcTemplate.query(SQL_GET_FILM_BY_ID, (rs) -> {
            if (rs.next()) {
                return makeFilm(rs);
            }
            return null;
            }, id));
        film.ifPresent(f -> {
            f.setLikes(getLikesByFilmId(id));
            f.setGenres(getGenresByFilmId(id));
        });
        return film;
    }

    @Override
    public void deleteFilmById(long id) {
        jdbcTemplate.update(SQL_DELETE_FILM, id);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, filmId, userId);
        getFilmById(filmId).ifPresent(film -> film.setLikes(getLikesByFilmId(filmId)));
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_DELETE_LIKE, filmId, userId);
        getFilmById(filmId).ifPresent(f -> f.setLikes(getLikesByFilmId(filmId)));
    }

    @Override
    public List<MPA> getAllMPA() {
        return jdbcTemplate.query(SQL_GET_ALL_MPA, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<MPA> getMPAById(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from MPA where MPA_id = ?", id);
        if(mpaRows.next()) {
            MPA mpa = new MPA();
            mpa.setId(mpaRows.getInt("MPA_id"));
            mpa.setName(mpaRows.getString("name"));
            mpa.setDescription(mpaRows.getString("description"));
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getAllGenre() {
        return jdbcTemplate.query(SQL_GET_ALL_GENRE, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);
        if(genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getInt("genre_id"));
            genre.setName(genreRows.getString("name"));
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    private void addGenre(Long film_id, Integer genre_id) {
        jdbcTemplate.update(SQL_ADD_GENRE, film_id, genre_id);
    }

    private void deleteGenres(Long film_id) {
        jdbcTemplate.update(SQL_DELETE_GENRE, film_id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("f_id"));
        film.setName(rs.getString("f_name"));
        film.setDescription(rs.getString("f_description"));
        film.setReleaseDate(rs.getDate("f_release_date").toLocalDate());
        film.setDuration(rs.getInt("f_duration"));
        MPA mpa = new MPA();
        mpa.setId(rs.getInt("m_id"));
        mpa.setName(rs.getString("m_name"));
        mpa.setDescription(rs.getString("m_description"));
        film.setMpa(mpa);
        return film;
    }

    public Set<Long> getLikesByFilmId(Long id) {
        List<Long> likes = jdbcTemplate.query(SQL_SELECT_LIKE_BY_FILM_ID,
                (rs, rowNum) -> rs.getLong("user_id"), id);
        return Set.copyOf(likes);
    }

    private Set<Genre> getGenresByFilmId(Long film_id) {
        List<Integer> genreIds = jdbcTemplate.query(SQL_GET_GENRE_BY_FILM_ID,
                (rs, rowNum) -> rs.getInt("genre_id"), film_id);
        if (genreIds.isEmpty()) {
            return new HashSet<>();
        } else {
            return genreIds.stream().map(id -> getGenreById(id)
                    .orElse(new Genre())).collect(Collectors.toSet());
        }
    }

    private MPA makeMpa(ResultSet rs) throws SQLException {
        MPA mpa = new MPA();
        mpa.setId(rs.getInt("MPA_id"));
        mpa.setName(rs.getString("name"));
        mpa.setDescription(rs.getString("description"));
        return mpa;
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    }
}
