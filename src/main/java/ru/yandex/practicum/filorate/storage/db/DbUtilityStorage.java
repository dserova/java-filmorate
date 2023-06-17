package ru.yandex.practicum.filorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;
import ru.yandex.practicum.filorate.model.Genre;
import ru.yandex.practicum.filorate.storage.UtilityStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("DbUtilityStorage")
public class DbUtilityStorage implements UtilityStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbUtilityStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenres() {

        final String sqlQuery = "SELECT * FROM public.GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, DbUtilityStorage::makeGenre);
        return genres;

    }

    @Override
    public Genre findGenresById(int id) {

        final String sqlQuery = "SELECT * FROM public.GENRE AS g WHERE g.genre_id = ?";

        final List<Genre> genres = jdbcTemplate.query(sqlQuery, DbUtilityStorage::makeGenre, id);

        if (genres.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (genres.size() > 1) {
            throw new IllegalStateException();
        }

        return genres.get(0);

    }

    @Override
    public List<AgeRatingSystem> findAllMpa() {

        final String sqlQuery = "SELECT * FROM public.age_rating_system";
        List<AgeRatingSystem> mpa = jdbcTemplate.query(sqlQuery, DbUtilityStorage::makeMpa);
        return mpa;

    }

    @Override
    public AgeRatingSystem findMpaById(int id) {

        final String sqlQuery = "SELECT * FROM public.age_rating_system AS age WHERE age.ars_id = ?";

        final List<AgeRatingSystem> mpa = jdbcTemplate.query(sqlQuery, DbUtilityStorage::makeMpa, id);

        if (mpa.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (mpa.size() > 1) {
            throw new IllegalStateException();
        }

        return mpa.get(0);

    }

    public static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }

    public static AgeRatingSystem makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new AgeRatingSystem(
                rs.getInt("ars_id"),
                rs.getString("name")
        );
    }

}
