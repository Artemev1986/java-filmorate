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
        getDirectorById(director.getId()); //Will throw an exception if there is no director with id
        directorStorage.updateDirector(director);
        log.debug("Director with id ({}) was updated", director.getId());
        return getDirectorById(director.getId());
    }


    public List<Director> getAllDirector() {
        log.debug("Get all Director");
        return directorStorage.getAllDirector();
    }

    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id).
                orElseThrow(() -> new NotFoundException("Director with id (" + id + ") not found")
                );
    }

    public void deleteDirectorById(long id) {
        getDirectorById(id); //Will throw an exception if there is no film with id
        directorStorage.deleteDirectorById(id);
        log.debug("Director with id ({}) was deleted", id);
    }


}
