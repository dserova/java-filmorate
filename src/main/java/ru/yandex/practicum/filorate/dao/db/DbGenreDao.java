package ru.yandex.practicum.filorate.dao.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbGenreStorage")
@Repository
public class DbGenreDao {

    private final JdbcTemplate jdbcTemplate;

    public DbGenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> findAllGenres() {

        final String sqlQuery = "SELECT * FROM public.GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, DbGenreDao::makeGenre);
        return genres;

    }

    public Genre findGenresById(int id) {

        final String sqlQuery = "SELECT * FROM public.GENRE AS g WHERE g.genre_id = ?";

        final List<Genre> genres = jdbcTemplate.query(sqlQuery, DbGenreDao::makeGenre, id);

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
