package ru.yandex.practicum.filorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.service.FilmService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        log.info("Получен запрос к эндпоинту create film");
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") Optional<Integer> count) {
        int size = count.get();
        return filmService.getPopular(size);
    }

    @GetMapping("/films/{Id}")
    public Film findFilm(@PathVariable("Id") Integer id) {
        return filmService.findById(id);
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
