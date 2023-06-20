package ru.yandex.practicum.filorate.dao.db;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filorate.dao.UserDao;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Component("dbUserDao")
@Repository
public class DbUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public DbUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {

        validation(user);

        String userName = getName(user);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO public.USER (email, login, name, birthday) values (?, ?, ?, ?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
                    stmt.setString(1, user.getEmail());
                    stmt.setString(2, user.getLogin());
                    stmt.setString(3, userName);
                    stmt.setDate(4, new java.sql.Date(user.getBirthday().getTime()));
                    return stmt;
                },
                keyHolder);

        user.setName(userName);
        user.setId((Integer) keyHolder.getKey());

        return user;

    }

    @Override
    public User update(User user) {

        findById(user.getId());
        validation(user);

        String userName = getName(user);

        String sqlQuery = "UPDATE public.USER SET email = ?, login = ?, name = ?, birthday = ?  WHERE user_id = ?";
        jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                userName,
                user.getBirthday(),
                user.getId()
        );

        user.setName(userName);

        return user;

    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM public.USER", DbUserDao::makeUser);
    }

    @Override
    public User findById(Integer id) {

        final String sqlQuery = "SELECT * FROM public.USER WHERE user_id = ?";
        final List<User> users = jdbcTemplate.query(sqlQuery, DbUserDao::makeUser, id);

        if (users.isEmpty()) {
            throw new DataNotFound("Has error response", null);
        } else if (users.size() > 1) {
            throw new IllegalStateException();
        }

        return users.get(0);

    }

    public static User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday"),
                new HashSet<>(),
                new HashSet<>()
        );
    }


    public void validation(User user) throws ValidationException {

        String email = user.getEmail();
        String login = user.getLogin();

        if (email.isEmpty() || !email.contains("@")) {
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @", user, HttpStatus.BAD_REQUEST);
        }
        if (login.isEmpty() || login.contains(" ")) {
            throw new ValidationException("логин не может быть пустым и содержать пробелы", user, HttpStatus.BAD_REQUEST);
        }
        if (user.getBirthday().after(Date.from(Instant.now()))) {
            throw new ValidationException("дата рождения не может быть в будущем.", user, HttpStatus.BAD_REQUEST);
        }

    }

    public String getName(User user) {
        String userName = user.getName();
        if (userName == null || userName.isEmpty()) {
            userName = user.getLogin();
        }
        return userName;
    }

}
