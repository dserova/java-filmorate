package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.exception.DataNotFound;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    private int id = 0;
    private HashMap<Integer, User> users = new HashMap<>();


    private Integer getNextID() {
        return ++id;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {

        log.info("Получен запрос к эндпоинту create user");

        validation(user);
        user.setId(getNextID());
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);

        return user;

    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {

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
            throw new DataNotFound("Пользователь не найден", user);
        }
    }

}
