package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.model.Genre;
import ru.yandex.practicum.filorate.storage.db.DbUtilityStorage;

import java.util.List;

@RestController
@Slf4j
public class UtilityController {

    private final DbUtilityStorage dbUtilityStorage;

    public UtilityController(DbUtilityStorage dbUserStorage) {
        this.dbUtilityStorage = dbUserStorage;
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return dbUtilityStorage.findAllGenres();
    }

    @GetMapping("/genres/{Id}")
    public Genre findGenresById(@PathVariable("Id") Integer id) {
        return dbUtilityStorage.findGenresById(id);
    }

    @GetMapping("/mpa")
    public List<AgeRatingSystem> findAllMpa() {
        return dbUtilityStorage.findAllMpa();
    }

    @GetMapping("/mpa/{Id}")
    public AgeRatingSystem findMpaById(@PathVariable("Id") Integer id) {
        return dbUtilityStorage.findMpaById(id);
    }

}
