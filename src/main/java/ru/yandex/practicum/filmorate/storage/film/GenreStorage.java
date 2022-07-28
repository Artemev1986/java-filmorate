package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAllGenre();
    Optional<Genre> getGenreById(Integer id);
}
