package ru.yandex.practicum.filorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;


/**
 * добавления и модификации объектов
 */

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private HashMap<Integer, Film> films = new HashMap();

    @Override
    public Film create(Film film) {
        validation(film);
        film.setId(getNextID());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {

        int id = film.getId();
        Film oldFilm = films.get(id);

        filmNotFound(oldFilm, film);
        validation(film);

        oldFilm.setName(film.getName());
        oldFilm.setDuration(film.getDuration());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setRate(film.getRate());

        return oldFilm;
    }

    @Override
    public List<Film> findAll() {
        return films.values().stream().collect(Collectors.toList());
    }

    @Override
    public Film findById(Integer id) {
        List<Film> filmList = films.values().stream().collect(Collectors.toList());
        return filmList.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .orElseThrow(() -> new DataNotFound("Has error response", null));
    }

    private Integer getNextID() {
        return ++id;
    }

    public List<Film> getPopular(int size) {

        List<Film> popularFilms = new ArrayList<>();

        for (Film film : films.values()) {
            if (film.getRate() > 0) {
                popularFilms.add(film);
            }
        }


        return popularFilms.stream()
                .sorted((p0, p1) -> p1.getRate().compareTo(p0.getRate()))
                .limit(size)
                .collect(Collectors.toList());
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
