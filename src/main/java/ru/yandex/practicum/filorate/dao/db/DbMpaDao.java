package ru.yandex.practicum.filorate.dao.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.model.AgeRatingSystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("dbMpaStorage")
@Repository
public class DbMpaDao {
    private final JdbcTemplate jdbcTemplate;

    public DbMpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AgeRatingSystem> findAllMpa() {

        final String sqlQuery = "SELECT * FROM public.age_rating_system";
        List<AgeRatingSystem> mpa = jdbcTemplate.query(sqlQuery, DbMpaDao::makeMpa);
        return mpa;

    }

    public AgeRatingSystem findMpaById(int id) {

        final String sqlQuery = "SELECT * FROM public.age_rating_system AS age WHERE age.ars_id = ?";

        final List<AgeRatingSystem> mpa = jdbcTemplate.query(sqlQuery, DbMpaDao::makeMpa, id);

        if (mpa.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (mpa.size() > 1) {
            throw new IllegalStateException();
        }

        return mpa.get(0);

    }

    public static AgeRatingSystem makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return new AgeRatingSystem(
                rs.getInt("ars_id"),
                rs.getString("name")
        );
    }

}
