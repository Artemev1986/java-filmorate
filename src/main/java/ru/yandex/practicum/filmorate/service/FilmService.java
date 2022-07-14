package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.LikeStorage;

import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> genreStorage.addGenre(film.getId(), genre.getId()));
        }
        log.debug("Adding new film with id: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId()); //Will throw an exception if there is no film with id
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            genreStorage.deleteGenres(film.getId());
            if (!film.getGenres().isEmpty())
                film.getGenres().forEach(genre -> genreStorage.addGenre(film.getId(), genre.getId()));
        }
        log.debug("Film with id ({}) was updated", film.getId());
        return getFilmById(film.getId());
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        films.forEach(f -> {
            f.setLikes(likeStorage.getLikesByFilmId(f.getId()));
            f.setGenres(genreStorage.getGenresByFilmId(f.getId()));
        });
        log.debug("Current film counts: {}", films.size());
        return films;
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film with id (" + id + ") not found"));
        film.setLikes(likeStorage.getLikesByFilmId(id));
        film.setGenres(genreStorage.getGenresByFilmId(id));
        log.debug("Film search by id: {}", id);
        return film;
    }

    public void deleteFilmById(long id) {
        getFilmById(id); //Will throw an exception if there is no film with id
        filmStorage.deleteFilmById(id);
        log.debug("Film with id ({}) was deleted", id);
    }

    public List<Film> getPopularFilms(long count) {
        List<Film> films = filmStorage.getPopularFilms(count);
        films.forEach(f -> {
            f.setLikes(likeStorage.getLikesByFilmId(f.getId()));
            f.setGenres(genreStorage.getGenresByFilmId(f.getId()));
        });
        log.debug("Get {} popular films", count);
        return films;
    }
}
