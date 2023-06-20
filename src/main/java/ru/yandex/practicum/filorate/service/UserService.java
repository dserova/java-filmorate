package ru.yandex.practicum.filorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.dao.UserDao;
import ru.yandex.practicum.filorate.dao.UserFriendsDao;
import ru.yandex.practicum.filorate.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final UserFriendsDao userFriendsDao;

    public User create(User user) {
        return userDao.create(user);
    }

    public User update(User user) {
        return userDao.update(user);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public User findById(Integer id) {
        return userDao.findById(id);
    }

    public void createFriends(int id1, int id2) {
        userFriendsDao.createFriends(id1, id2);
    }

    public void deleteFriends(int id1, int id2) {
        userFriendsDao.deleteFriends(id1, id2);
    }

    public List<User> getAllFriends(int id) {
        return userFriendsDao.getAllFriends(id);
    }

    public List<User> getCommonFriends(int id1, int id2) {
        return userFriendsDao.getCommonFriends(id1, id2);
    }

}
