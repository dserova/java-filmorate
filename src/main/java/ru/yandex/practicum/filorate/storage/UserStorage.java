package ru.yandex.practicum.filorate.storage;

import ru.yandex.practicum.filorate.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> findAll();

    User findById(Integer id);

}
