package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;


import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;
    private final FilmDirectorsStorage filmDirectorsStorage;
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, DirectorStorage directorStorage, UserStorage userStorage,
                       FilmDirectorsStorage filmDirectorsStorage, FilmGenreStorage filmGenreStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.userStorage = userStorage;
        this.filmDirectorsStorage = filmDirectorsStorage;
        this.filmGenreStorage = filmGenreStorage;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            filmGenreStorage.addGenres(film.getId(), List.copyOf(film.getGenres()));
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            filmDirectorsStorage.addDirectors(film.getId(), List.copyOf(film.getDirectors()));
        }
        Film filmFromStorage = getFilmById(film.getId());
        log.debug("Adding new film with id: {}", film.getId());
        return filmFromStorage;
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId()); //Will throw an exception if there is no film with id
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            filmGenreStorage.deleteGenres(film.getId());
            if (!film.getGenres().isEmpty())
                filmGenreStorage.addGenres(film.getId(), List.copyOf(film.getGenres()));
        } else {
            filmGenreStorage.deleteGenres(film.getId());
        }

        if (film.getDirectors() != null) {
            filmDirectorsStorage.deleteDirector(film.getId());
            if (!film.getDirectors().isEmpty())
                filmDirectorsStorage.addDirectors(film.getId(), List.copyOf(film.getDirectors()));
        } else {
            filmDirectorsStorage.deleteDirector(film.getId());
        }
        Film filmFromStorage = filmStorage.getFilmById(film.getId()).orElseThrow(() -> new NotFoundException("Film with id (" + film.getId() + ") not found"));
        if (filmFromStorage.getDirectors().isEmpty()) {
            filmFromStorage.setDirectors(null);
        }
        log.debug("Film with id ({}) was updated", film.getId());
        return filmFromStorage;
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        log.debug("Current film counts: {}", films.size());
        return films;
    }


    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with id (" + id + ") not found"));
        log.debug("Film search by id: {}", id);
        return film;
    }

    public void deleteFilmById(long id) {
        filmStorage.deleteFilmById(id);
        log.debug("Film with id ({}) was deleted", id);
    }

    public List<Film> getPopularFilms(long count, Long year, Long genreId) {
        List<Film> films = filmStorage.getPopularFilms(count, year, genreId);
        log.debug("Get {} popular films", count);
        return films;
    }

    public List<Film> getDirectorFilmsSortYear(long id) {
        directorStorage.getDirectorById(id).orElseThrow(() -> new NotFoundException("Director with id (" + id + ") not found"));
        List<Film> films = filmStorage.getDirectorFilmsSortYear(id);
        log.debug("Get director {} sorted films by release date", id);
        return films;
    }

    public List<Film> getDirectorFilmsSortLikes(long id) {
        directorStorage.getDirectorById(id).orElseThrow(() -> new NotFoundException("Director with id (" + id + ") not found"));
        List<Film> films = filmStorage.getDirectorFilmsSortLikes(id);
        log.debug("Get director {} sorted films by likes", id);
        return films;
    }

    public List<Film> searchForFilms(String query, String by) {
        List<Film> films = filmStorage.searchForFilms(query, by);
        log.debug("Get search {} query = " + query + " by = " + by);
        return films;
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id (" + userId + ") not found"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User with id (" + friendId + ") not found"));
        List<Film> films = filmStorage.getCommonFilms(userId, friendId);
        log.debug("Get common films between users id={} and id={} sorted films by likes", userId, friendId);
        return films;
    }
}
