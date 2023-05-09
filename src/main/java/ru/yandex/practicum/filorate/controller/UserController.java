package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> create(@RequestBody User user) throws ValidationException {

        log.info("Получен запрос к эндпоинту create user");

        if (validation(user)) {
            user.setId(getNextID());
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }

        return new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);

    }

    @PutMapping(value = "/users")
    public ResponseEntity<?> update(@RequestBody User user) throws ValidationException {

        int id = user.getId();
        User oldUser = users.get(id);
        if (oldUser != null) {
            if (validation(user)) {
                if (user.getName() == null || user.getName().isEmpty()) {
                    oldUser.setName(user.getLogin());
                } else {
                    oldUser.setName(user.getName());
                }
                oldUser.setLogin(user.getLogin());
                oldUser.setEmail(user.getEmail());
                oldUser.setBirthday(user.getBirthday());
            } else {
                return new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<User>(user, HttpStatus.OK);

    }

    public Boolean validation(User user) throws ValidationException {

        Boolean rez = true;

        String email = user.getEmail();
        String login = user.getLogin();

        try {
            if (email.isEmpty() || !email.contains("@")) {
                throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
            }
            if (login.isEmpty() || login.contains(" ")) {
                throw new ValidationException("логин не может быть пустым и содержать пробелы");
            }
            if (user.getBirthday().after(Date.from(Instant.now()))) {
                throw new ValidationException("дата рождения не может быть в будущем.");
            }
        } catch (ValidationException e) {
            rez = false;
            log.info(e.getMessage());
        }

        return rez;
    }
}
