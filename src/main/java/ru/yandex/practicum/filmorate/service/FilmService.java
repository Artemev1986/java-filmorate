package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void deleteFilmById(long id) {
        filmStorage.deleteFilmById(id);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(long filmId, long userId) {
        userStorage.getUserById(userId); //Will throw an exception if there is no user with id
        filmStorage.getFilmById(filmId).addLike(userId);
        log.debug("Like for the {} added by {}",
                filmStorage.getFilmById(filmId).getName(),
                userStorage.getUserById(userId).getName());
    }

    public void deleteLike(long filmId, long userId) {
        userStorage.getUserById(userId); //Will throw an exception if there is no user with id
        filmStorage.getFilmById(filmId).deleteLike(userId);
        log.debug("Like for the {} deleted by {}",
                filmStorage.getFilmById(filmId).getName(),
                userStorage.getUserById(userId).getName());
    }

    public List<Film> getPopularFilms(Long count) {
        log.debug("Get {} popular films", count);
        return filmStorage.findAll()
                .stream()
                .sorted((p0, p1) -> Long.compare(p1.getLikes().size(), p0.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
