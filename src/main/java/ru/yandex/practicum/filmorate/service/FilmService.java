package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
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

    public List<Film> findAll() {
        log.debug("Current film counts: {}", filmStorage.findAll().size());
        return filmStorage.findAll();
    }

    public void addLike(long filmId, long userId) {
        userService.getUserById(userId); //Will throw an exception if there is no user with id
        getFilmById(filmId).addLike(userId);
        log.debug("Like for the {} added by {}",
                getFilmById(filmId).getName(),
                userService.getUserById(userId).getName());
    }

    public void deleteLike(long filmId, long userId) {
        userService.getUserById(userId); //Will throw an exception if there is no user with id
        getFilmById(filmId).deleteLike(userId);
        log.debug("Like for the {} deleted by {}",
                getFilmById(filmId).getName(),
                userService.getUserById(userId).getName());
    }

    public List<Film> getPopularFilms(long count) {
        log.debug("Get {} popular films", count);
        return filmStorage.findAll()
                .stream()
                .sorted((p0, p1) -> Long.compare(p1.getLikes().size(), p0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
