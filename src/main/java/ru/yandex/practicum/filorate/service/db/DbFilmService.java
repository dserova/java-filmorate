package ru.yandex.practicum.filorate.service.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.service.FilmService;
import ru.yandex.practicum.filorate.storage.db.DbFilmStorage;
import ru.yandex.practicum.filorate.storage.db.DbUserStorage;

import java.sql.PreparedStatement;

/**
 * добавить, удалить лайк.
 */

@Repository
public class DbFilmService implements FilmService {

    private final JdbcTemplate jdbcTemplate;
    public DbFilmStorage dbFilmStorage;
    public DbUserStorage dbUserStorage;

    public DbFilmService(JdbcTemplate jdbcTemplate, DbFilmStorage dbFilmStorage, DbUserStorage dbUserStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbFilmStorage = dbFilmStorage;
        this.dbUserStorage = dbUserStorage;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

        Film film = dbFilmStorage.findById(filmId);
        dbUserStorage.findById(userId);

        int rate;
        if (film.getRate() == null) {
            rate = 1;
        } else {
            rate = film.getRate() + 1;
        }

        String sqlQuery = "UPDATE public.FILM SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                rate,
                filmId
        );


        String sqlMerge = "MERGE INTO public.USER_FILMS_LIKES (user_user_id, films_likes_film_id) values (?, ?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlMerge);
                    stmt.setInt(1, userId);
                    stmt.setInt(2, filmId);
                    return stmt;
                }
        );

    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {

        Film film = dbFilmStorage.findById(filmId);
        dbUserStorage.findById(userId);

        int rate;
        if (film.getRate() == null || film.getRate() == 0) {
            return;
        } else {
            rate = film.getRate() - 1;
        }

        String sqlQuery = "UPDATE public.FILM SET rate = ? WHERE film_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                rate,
                filmId
        );


        String sqlDelete = "DELETE FROM public.USER_FILMS_LIKES WHERE user_user_id = ? AND films_likes_film_id = ? ";
        jdbcTemplate.update(sqlDelete, userId, filmId);

    }

}
