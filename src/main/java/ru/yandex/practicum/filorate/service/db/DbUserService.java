package ru.yandex.practicum.filorate.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.service.UserService;
import ru.yandex.practicum.filorate.storage.db.DbUserStorage;

import java.sql.PreparedStatement;
import java.util.List;

/**
 * добавление в друзья, удаление из друзей, вывод списка общих друзей.
 */

@Service
public class DbUserService implements UserService {

    private final JdbcTemplate jdbcTemplate;
    public DbUserStorage dbUserStorage;

    @Autowired
    public DbUserService(JdbcTemplate jdbcTemplate, DbUserStorage dbUserStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbUserStorage = dbUserStorage;
    }

    public void createFriends(int id1, int id2) {

        dbUserStorage.findById(id1);
        dbUserStorage.findById(id2);

        String sqlQuery = "INSERT INTO public.USER_FRIENDS (user_user_id, friends_user_id) values (?, ?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                    stmt.setInt(1, id1);
                    stmt.setInt(2, id2);
                    return stmt;
                }
        );
    }

    public void deleteFriends(int id1, int id2) {

        String sqlQuery = "DELETE FROM public.USER_FRIENDS WHERE user_user_id = ? AND friends_user_id = ?";
        jdbcTemplate.update(sqlQuery, id1, id2);

    }

    public List<User> getAllFriends(int id) {

        String sql = "SELECT * " +
                "FROM public.USER AS u " +
                "INNER JOIN " +
                "public.USER_FRIENDS AS f ON u.user_id = f.friends_user_id AND f.user_user_id = ?";

        List<User> friends = jdbcTemplate.query(sql, (rs, rowNum) -> dbUserStorage.makeUser(rs, 0), id);

        return friends;
    }

    public List<User> getCommonFriends(int id1, int id2) {

        String sql = "SELECT * " +
                "FROM public.USER AS u WHERE user_id IN (" +
                "SELECT friends_user_id FROM public.USER_FRIENDS WHERE user_user_id IN (?,?) GROUP BY friends_user_id HAVING COUNT(user_user_id)>1 " +
                ")";

        List<User> friends = jdbcTemplate.query(sql, (rs, rowNum) -> dbUserStorage.makeUser(rs, 0), id1, id2);

        return friends;

    }

}
