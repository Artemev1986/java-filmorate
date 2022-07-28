package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;

public interface FilmGenreStorage {
    void addGenre(Long film_id, Integer genre_id);
    void deleteGenres(Long film_id);
    Set<Genre> getGenresByFilmId(Long film_id);
}
