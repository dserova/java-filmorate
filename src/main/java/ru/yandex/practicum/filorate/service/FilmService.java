package ru.yandex.practicum.filorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.storage.FilmStorage;
import ru.yandex.practicum.filorate.storage.UserStorage;

/**
 * добавить, удалить лайк.
 */

@Service
public class FilmService {

    public FilmStorage filmStorage;
    public UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer id, Integer userId) {

        User user = userStorage.findById(userId);
        Film film = filmStorage.findById(id);

        if (!user.getFilmsLikes().contains(id)) {
            user.addLike(id);
            film.addLike();
        }

    }

    public void deleteLike(Integer id, Integer userId) {

        User user = userStorage.findById(userId);
        Film film = filmStorage.findById(id);

        if (!user.getFilmsLikes().contains(id)) {
            user.deleteLike(id);
            film.deleteLike();
        }

    }

}
