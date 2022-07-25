package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;


import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;


    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;

        this.directorStorage = directorStorage;

    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(genre -> genreStorage.addGenre(film.getId(), genre.getId()));
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            film.getDirectors().forEach(directors -> directorStorage.addDirectorByFilm(film.getId(), directors.getId()));
        }
        log.debug("Adding new film with id: {}", film.getId());
        return getFilmById(film.getId());
    }

    public Film updateFilm(Film film) {
        getFilmById(film.getId()); //Will throw an exception if there is no film with id
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            genreStorage.deleteGenres(film.getId());
            if (!film.getGenres().isEmpty())
                film.getGenres().forEach(genre -> genreStorage.addGenre(film.getId(), genre.getId()));
        }
        if (film.getDirectors() != null) {
            directorStorage.deleteDirector(film.getId());
            if (!film.getDirectors().isEmpty())
                film.getDirectors().forEach(directors -> directorStorage.addDirectorByFilm(film.getId(), directors.getId()));
        } else {
            directorStorage.deleteDirector(film.getId());
        }
        Film film1 = filmStorage.getFilmById(film.getId()).orElseThrow(() -> new NotFoundException("Film with id (" + film.getId() + ") not found"));
        if (film1.getDirectors().isEmpty()) {
            film1.setDirectors(null);
        }
        log.debug("Film with id ({}) was updated", film.getId());
        return film1;
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
        getFilmById(id); //Will throw an exception if there is no film with id
        filmStorage.deleteFilmById(id);
        log.debug("Film with id ({}) was deleted", id);
    }

    public List<Film> getPopularFilms(long count) {
        List<Film> films = filmStorage.getPopularFilms(count);
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

    public List<Film> searchForFilms(String query, String by){
        return filmStorage.searchForFilms(query, by);
    }
}
