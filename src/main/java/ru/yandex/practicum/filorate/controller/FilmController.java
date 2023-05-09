package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> create(@RequestBody Film film) throws ValidationException {

        log.info("Получен запрос к эндпоинту create film");

        if (validation(film)) {
            film.setId(getNextID());
            films.put(film.getId(), film);
            return new ResponseEntity<Film>(film, HttpStatus.OK);
        } else {
            return new ResponseEntity<Film>(film, HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping(value = "/films")
    public ResponseEntity<?> update(@RequestBody Film film) throws ValidationException {

        int id = film.getId();
        Film oldFilm = films.get(id);
        if (oldFilm != null) {
            if (validation(film)) {
                oldFilm.setName(film.getName());
                oldFilm.setDuration(film.getDuration());
                oldFilm.setDescription(film.getDescription());
                oldFilm.setReleaseDate(film.getReleaseDate());
            } else {
                return new ResponseEntity<Film>(film, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<Film>(film, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Film>(film, HttpStatus.OK);
    }


    public Boolean validation(Film film) throws ValidationException {

        Boolean rez = true;

        try {
            if (film.getName() == null || film.getName().isEmpty()) {
                throw new ValidationException("название не может быть пустым");
            }
            if (film.getDescription().length() > 200) {
                throw new ValidationException("максимальная длина описания — 200 символов");
            }
            if (film.getReleaseDate().before(new Date(-5, 12, 28))) {
                throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
            }
            if (film.getDuration() < 0) {
                throw new ValidationException("продолжительность фильма должна быть положительной");
            }
        } catch (ValidationException e) {
            rez = false;
            log.info(e.getMessage());
        }

        return rez;
    }

}