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
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage dbFilmStorage, FilmService dbFilmService) {
        this.filmStorage = dbFilmStorage;
        this.filmService = dbFilmService;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос к эндпоинту create film");
        return filmStorage.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Optional<Integer> count) {
        int size = count.get();
        return filmStorage.getPopular(size);
    }

    @GetMapping("/films/{Id}")
    public Film findFilm(@PathVariable("Id") Integer id) {
        return filmStorage.findById(id);
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
