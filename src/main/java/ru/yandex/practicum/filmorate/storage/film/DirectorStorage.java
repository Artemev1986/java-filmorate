package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    void addDirector(Director director);
    void addDirectorByFilm(Long film_id, Long director_id );
    void updateDirector(Director director);
    Set<Director> getDirectorByFilmId(Long film_id);
    List<Director> getAllDirector();
    Optional<Director> getDirectorById(long id);
    void deleteDirectorById(long id);
    void deleteDirector(Long film_id);


}
