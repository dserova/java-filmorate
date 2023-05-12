package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.Film;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private int id = 0;
    private HashMap<Integer, Film> films = new HashMap();

    private Integer getNextID() {
        return ++id;
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {

        log.info("Получен запрос к эндпоинту create film");

        validation(film);
        film.setId(getNextID());
        films.put(film.getId(), film);
        return film;

    }

    @PutMapping(value = "/films")
    public Film update(@RequestBody Film film) {

        int id = film.getId();
        Film oldFilm = films.get(id);

        filmNotFound(oldFilm, film);
        validation(film);

        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());

        return oldFilm;

    }

    public void validation(Film film) throws ValidationException {

        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("название не может быть пустым", film, HttpStatus.BAD_REQUEST);
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("максимальная длина описания — 200 символов", film, HttpStatus.BAD_REQUEST);
        }
        if (film.getReleaseDate().before(new Date(-5, 12, 28))) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года", film, HttpStatus.BAD_REQUEST);
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("продолжительность фильма должна быть положительной", film, HttpStatus.BAD_REQUEST);
        }

    }

    public void filmNotFound(Film oldFilm, Film film) throws DataNotFound {
        if (oldFilm == null) {
            throw new DataNotFound("Фильм не найден", film);
        }
    }

}
