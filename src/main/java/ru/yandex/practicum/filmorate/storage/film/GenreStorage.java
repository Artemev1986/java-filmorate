package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenre();
    Optional<Genre> getGenreById(Integer id);
    Set<Genre> getGenresByFilmId(Long film_id);
    void addGenre(Long film_id, Integer genre_id);
    void deleteGenres(Long film_id);
}
