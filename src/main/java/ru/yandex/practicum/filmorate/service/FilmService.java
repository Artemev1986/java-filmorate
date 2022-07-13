package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private  final GenreStorage genreStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage")
                           FilmStorage filmStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage,
                       UserService userService) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        log.debug("Adding new film with id: {}", film.getId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId()); //Will throw an exception if there is no film with id
        log.debug("Film with id ({}) was updated", film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(long id) {
        log.debug("Film search by id: {}", id);
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with id (" + id + ") not found"));
    }

    public void deleteFilmById(long id) {
        getFilmById(id); //Will throw an exception if there is no film with id
        filmStorage.deleteFilmById(id);
        log.debug("Film with id ({}) was deleted", id);
    }

    public List<Film> findAllFilms() {
        log.debug("Current film counts: {}", filmStorage.findAllFilms().size());
        return filmStorage.findAllFilms();
    }

    public void addLike(long filmId, long userId) {
        userService.getUserById(userId); //Will throw an exception if there is no user with id
        getFilmById(filmId);
        filmStorage.addLike(filmId, userId);
        log.debug("Like for the {} added by {}",
                getFilmById(filmId).getName(),
                userService.getUserById(userId).getName());
    }

    public void deleteLike(long filmId, long userId) {
        userService.getUserById(userId); //Will throw an exception if there is no user with id
        getFilmById(filmId);
        filmStorage.deleteLike(filmId, userId);
        log.debug("Like for the {} deleted by {}",
                getFilmById(filmId).getName(),
                userService.getUserById(userId).getName());
    }

    public List<Film> getPopularFilms(long count) {
        log.debug("Get {} popular films", count);
        return filmStorage.getPopularFilms(count);
    }

    public List<Mpa> getAllMpa() {
        log.debug("Get all MPA");
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id).
                orElseThrow(() -> new NotFoundException("MPA with id (" + id + ") not found")
        );
    }

    public  List<Genre> getAllGenre() {
        log.debug("Get all Genre");
        return genreStorage.getAllGenre();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id).
                orElseThrow(() -> new NotFoundException("Genre with id (" + id + ") not found")
        );
    }
}
