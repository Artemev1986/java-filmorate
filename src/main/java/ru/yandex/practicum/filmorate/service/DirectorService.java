package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorStorage;

import java.util.List;


@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director addDirector(Director director) {
        directorStorage.addDirector(director);
        log.debug("Adding new director with id: {}", director.getId());
        return director;
    }

    public Director updateDirector(Director director) {
        int updateDirector = directorStorage.updateDirector(director);
        if (updateDirector < 1) {
            throw new NotFoundException("Director not found");
        }
        log.debug("Director with id ({}) was updated", director.getId());
        return director;
    }


    public List<Director> getAllDirector() {
        List<Director> directors = directorStorage.getAllDirector();
        log.debug("Get all Director");
        return directors;
    }

    public Director getDirectorById(long id) {
        Director director = directorStorage.getDirectorById(id).
                orElseThrow(() -> new NotFoundException("Director with id (" + id + ") not found")
                );
        log.debug("Get Director by id({})", id);
        return director;
    }

    public void deleteDirectorById(long id) {
        getDirectorById(id); //Will throw an exception if there is no director with id
        directorStorage.deleteDirectorById(id);
        log.debug("Director with id ({}) was deleted", id);
    }


}
