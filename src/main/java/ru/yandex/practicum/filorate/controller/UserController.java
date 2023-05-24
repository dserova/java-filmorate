package ru.yandex.practicum.filorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.service.UserService;
import ru.yandex.practicum.filorate.storage.UserStorage;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage inMemoryUserStorage;
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/users/{Id}")
    public User findUser(@PathVariable("Id") Integer id) {
        return inMemoryUserStorage.findById(id);
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        log.info("Получен запрос к эндпоинту create user");
        return inMemoryUserStorage.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) {
        return inMemoryUserStorage.update(user);
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
