package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {

    void addDirector(Director director);
    void updateDirector(Director director);
    List<Director> getAllDirector();
    Optional<Director> getDirectorById(long id);
    void deleteDirectorById(long id);
}
