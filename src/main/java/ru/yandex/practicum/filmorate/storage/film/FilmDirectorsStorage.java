package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Set;

public interface FilmDirectorsStorage {
    void addDirectorByFilm(Long film_id, Long director_id);
    Set<Director> getDirectorByFilmId(Long film_id);
    void deleteDirector(Long film_id);
}
