package ru.yandex.practicum.filorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.service.UserService;
import ru.yandex.practicum.filorate.storage.UserStorage;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserStorage userStorage; //определится через @Primary
    private final UserService userService; //определится через @Qualifier

    public UserController(UserStorage storage, @Qualifier("dbUserService") UserService userService) {
        this.userStorage = storage;
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/users/{Id}")
    public User findUser(@PathVariable("Id") Integer id) {
        return userStorage.findById(id);
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту create user");
        return userStorage.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        return userStorage.update(user);
    }

    @PutMapping("/users/{Id}/friends/{id2}")
    public void createFriends(@PathVariable("Id") Integer id1, @PathVariable("id2") Integer id2) {
        userService.createFriends(id1, id2);
    }

    @DeleteMapping("/users/{Id}/friends/{id2}")
    public void deleteFriends(@PathVariable("Id") Integer id1, @PathVariable("id2") Integer id2) {
        userService.deleteFriends(id1, id2);
    }

    @GetMapping("/users/{Id}/friends")
    public List<User> getAllFriends(@PathVariable("Id") Integer id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{Id}/friends/common/{id2}")
    public List<User> getCommonFriends(@PathVariable("Id") Integer id1, @PathVariable("id2") Integer id2) {
        return userService.getCommonFriends(id1, id2);
    }

}
