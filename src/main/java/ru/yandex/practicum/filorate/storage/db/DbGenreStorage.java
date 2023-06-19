package ru.yandex.practicum.filorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbGenreStorage")
public class DbGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAllGenres() {

        final String sqlQuery = "SELECT * FROM public.GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, DbGenreStorage::makeGenre);
        return genres;

    }

    public Genre findGenresById(int id) {

        final String sqlQuery = "SELECT * FROM public.GENRE AS g WHERE g.genre_id = ?";

        final List<Genre> genres = jdbcTemplate.query(sqlQuery, DbGenreStorage::makeGenre, id);

        if (genres.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (genres.size() > 1) {
            throw new IllegalStateException();
        }

        return genres.get(0);

    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }

}
