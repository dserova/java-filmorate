package ru.yandex.practicum.filorate.dao;

import ru.yandex.practicum.filorate.model.Film;

import java.util.List;

public interface FilmDao {

    Film create(Film film);

    Film update(Film film);

    List<Film> findAll();

    Film findById(Integer id);

    List<Film> getPopular(int n);

}
