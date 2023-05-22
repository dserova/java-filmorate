package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.service.FilmService;
import ru.yandex.practicum.filorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class FilmController {
    private final FilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.inMemoryFilmStorage = filmService.filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return inMemoryFilmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос к эндпоинту create film");
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return inMemoryFilmStorage.update(film);
    }

    //@GetMapping(value = {"/films/popular", "/films/popular/{n}"})
    @GetMapping(value = "/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Optional<Integer> count) {
        int size = count.get();
        return inMemoryFilmStorage.getPopular(size);
    }

    @GetMapping("/films/{Id}")
    public Film findFilm(@PathVariable("Id") Integer id) {
        return inMemoryFilmStorage.findById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(id, userId);
    }

}
