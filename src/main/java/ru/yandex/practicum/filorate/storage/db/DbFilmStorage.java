package ru.yandex.practicum.filorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.model.Film;
import ru.yandex.practicum.filorate.model.Genre;
import ru.yandex.practicum.filorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * добавления и модификации объектов
 */

@Primary
@Component("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DbUtilityStorage dbUtilityStorage;

    public DbFilmStorage(JdbcTemplate jdbcTemplate, DbUtilityStorage dbUtilityStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbUtilityStorage = dbUtilityStorage;
    }

    @Override
    public Film create(Film film) {

        validation(film);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO public.FILM (description, name, release_date, duration, rate, mpa_ars_id) values (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
                    stmt.setString(1, film.getDescription());
                    stmt.setString(2, film.getName());
                    stmt.setDate(3, new java.sql.Date(film.getReleaseDate().getTime()));
                    stmt.setInt(4, film.getDuration());
                    stmt.setObject(5, film.getRate());
                    stmt.setObject(6, film.getMpa().getId());
                    return stmt;
                },
                keyHolder);


        film.setId((Integer) keyHolder.getKey());

        setGenre(film);

        return film;

    }

    @Override
    public Film update(Film film) {

        findById(film.getId());
        validation(film);

        String sqlQuery = "UPDATE public.FILM SET description = ?, name = ?, release_date = ?, duration = ?, rate = ?, mpa_ars_id = ? WHERE film_id = ?";

        jdbcTemplate.update(
                sqlQuery,
                film.getDescription(),
                film.getName(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
        );

        setGenre(film);

        return film;

    }

    @Override
    public List<Film> findAll() {

        final String sqlQuery = "SELECT \n" +
                "films.*,\n" +
                "ars.name AS age_name \n" +
                "FROM public.FILM AS films \n" +
                "INNER JOIN\n" +
                "public.age_rating_system AS ars ON ars.ars_id= films.mpa_ars_id";

        List<Film> films = jdbcTemplate.query(sqlQuery, DbFilmStorage::makeFilm);

        if (films != null && films.size() != 0) {
            for (Film film : films) {
                film.setGenres(getGenres(film.getId()));
            }
        }

        return films;
    }

    @Override
    public Film findById(Integer id) {

        final String sqlQuery = "SELECT \n" +
                "films.*,\n" +
                "ars.name AS age_name \n" +
                "FROM public.FILM AS films \n" +
                "INNER JOIN\n" +
                "public.age_rating_system AS ars ON ars.ars_id= films.mpa_ars_id  \n" +
                "WHERE films.film_id = ?";


        final List<Film> films = jdbcTemplate.query(sqlQuery, DbFilmStorage::makeFilm, id);

        if (films.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (films.size() > 1) {
            throw new IllegalStateException();
        }

        Film film = films.get(0);
        film.setGenres(getGenres(id));

        return film;

    }

    @Override
    public List<Film> getPopular(int n) {

        String sqlQuery = "SELECT \n" +
                "films.*,\n" +
                "ars.name AS age_name\n" +
                "FROM public.FILM AS films \n" +
                "INNER JOIN \n" +
                "public.age_rating_system AS ars ON ars.ars_id= films.mpa_ars_id\n" +
                "ORDER BY films.rate DESC\n" +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, DbFilmStorage::makeFilm, n);

        if (films != null && films.size() != 0) {
            for (Film film : films) {
                film.setGenres(getGenres(film.getId()));
            }
        }

        return films;

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

    public static Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date"),
                rs.getInt("duration"),
                rs.getInt("rate"),
                new ArrayList<>(),
                new AgeRatingSystem(rs.getInt("mpa_ars_id"), rs.getString("age_name"))
        );
    }

    void setGenre(Film film) {

        int film_id = film.getId();
        List<Genre> genres = (List<Genre>) film.getGenres();

        String sqlDelete = "DELETE FROM public.FILM_GENRES WHERE film_film_id = ? ";
        jdbcTemplate.update(sqlDelete, film_id);

        if (genres == null || genres.size() == 0) {
            film.setGenres(getGenres(film_id));
            return;
        }

        String sqlQuery = "MERGE INTO public.FILM_GENRES (film_film_id, genres_genre_id) values (?, ?)";

        for (Genre genre : genres) {

            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                        stmt.setInt(1, film_id);
                        stmt.setInt(2, genre.getId());
                        return stmt;
                    }
            );
        }

        film.setGenres(getGenres(film_id));

    }

    List<Genre> getGenres(int film_id) {

        String sql = "SELECT g.genre_id , g.name  \n" +
                "FROM public.GENRE AS g\n" +
                "INNER JOIN\n" +
                "public.FILM_GENRES AS fgs ON fgs.genres_genre_id = g.genre_id  AND fgs.film_film_id = ?";

        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> dbUtilityStorage.makeGenre(rs, 0), film_id);

        return genres;
    }

}
