package ru.yandex.practicum.filorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.dao.FilmDao;
import ru.yandex.practicum.filorate.dao.FilmLikeDao;
import ru.yandex.practicum.filorate.model.Film;

import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmDao filmDao;
    private final FilmLikeDao filmLikeDao;

    public Film create(Film film) {
        return filmDao.create(film);
    }

    public Film update(Film film) {
        return filmDao.update(film);
    }

    public List<Film> findAll() {
        return filmDao.findAll();
    }

    public Film findById(Integer id) {
        return filmDao.findById(id);
    }

    public List<Film> getPopular(int n) {
        return filmDao.getPopular(n);
    }

    public void addLike(Integer filmId, Integer userId) {
        filmLikeDao.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmLikeDao.deleteLike(filmId, userId);
    }

}
