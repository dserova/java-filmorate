package ru.yandex.practicum.filorate.dao.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.dao.FilmLikeDao;

import java.sql.PreparedStatement;

/**
 * добавить, удалить лайк.
 */

@Repository
@Component("dbFilmLikeDao")
public class DbFilmLikeDao implements FilmLikeDao {

    private final JdbcTemplate jdbcTemplate;
    public DbFilmDao dbFilmDao;
    public DbUserDao dbUserDao;

    public DbFilmLikeDao(JdbcTemplate jdbcTemplate, DbFilmDao dbFilmDao, DbUserDao dbUserDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbFilmDao = dbFilmDao;
        this.dbUserDao = dbUserDao;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {

        Film film = dbFilmDao.findById(filmId);
        dbUserDao.findById(userId);

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

        Film film = dbFilmDao.findById(filmId);
        dbUserDao.findById(userId);

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
