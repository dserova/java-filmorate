package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filorate.model.Genre;
import ru.yandex.practicum.filorate.storage.db.DbGenreStorage;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final DbGenreStorage dbGenreStorage;

    public GenreController(DbGenreStorage dbGenreStorage) {
        this.dbGenreStorage = dbGenreStorage;
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return dbGenreStorage.findAllGenres();
    }

    @GetMapping("/genres/{Id}")
    public Genre findGenresById(@PathVariable("Id") Integer id) {
        return dbGenreStorage.findGenresById(id);
    }

}
