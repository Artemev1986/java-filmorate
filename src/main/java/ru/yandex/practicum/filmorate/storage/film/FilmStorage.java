package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> findAllFilms();

    Optional<Film> getFilmById(long id);

    void deleteFilmById(long id);

    List<Film> getPopularFilms(long count, Long year, Long genreId);

    List<Film> getDirectorFilmsSortYear(long id);

    List<Film> getDirectorFilmsSortLikes(long id);

    List<Film> searchForFilms(String query, String by);

    List<Film> getCommonFilms(long userId, long friendId);
}
