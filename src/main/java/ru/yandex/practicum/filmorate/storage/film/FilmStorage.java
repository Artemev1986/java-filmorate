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

    List<Film> getPopularFilms(long count);

    List<Film> getDirectorFilmsSortYear(long id);

    List<Film> getDirectorFilmsSortLikes(long id);
}
