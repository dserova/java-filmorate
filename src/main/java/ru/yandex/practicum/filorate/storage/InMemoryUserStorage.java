package ru.yandex.practicum.filorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * добавления и модификации объектов
 */

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 0;
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {

        validation(user);
        user.setId(getNextID());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setFriends(new HashSet<>());
        user.setFilmsLikes(new HashSet<>());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(User user) {

        int id = user.getId();
        User oldUser = users.get(id);

        userNotFound(oldUser, user);
        validation(user);

        if (user.getName() == null || user.getName().isEmpty()) {
            oldUser.setName(user.getLogin());
        } else {
            oldUser.setName(user.getName());
        }
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthday(user.getBirthday());

        return oldUser;

    }

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public User findById(Integer id) {
        List<User> usersList = users.values().stream().collect(Collectors.toList());
        return usersList.stream()
                .filter(p -> Objects.equals(p.getId(), id))
                .findFirst()
                .orElseThrow(() -> new DataNotFound("Has error response", null));
    }

    private Integer getNextID() {
        return ++id;
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

    public void userNotFound(User oldUser, User user) throws DataNotFound {
        if (oldUser == null) {
            throw new DataNotFound("Has error response", user);
        }
    }

}
