package ru.yandex.practicum.filorate.service.inMemory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.service.FilmService;
import ru.yandex.practicum.filorate.storage.FilmStorage;
import ru.yandex.practicum.filorate.storage.UserStorage;

/**
 * добавить, удалить лайк.
 */

@Service
public class InMemoryFilmService implements FilmService {

    public FilmStorage filmStorage;
    public UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.filmStorage = inMemoryFilmStorage;
        this.userStorage = inMemoryUserStorage;
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
