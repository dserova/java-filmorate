package ru.yandex.practicum.filorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filorate.model.User;
import ru.yandex.practicum.filorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * добавление в друзья, удаление из друзей, вывод списка общих друзей.
 */

@Service
public class UserService {

    public UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userService) {
        this.userStorage = userService;
    }

    public void createFriends(int id1, int id2) {
        User user1 = userStorage.findById(id1);
        User user2 = userStorage.findById(id2);
        user1.addFriend(id2);
        user2.addFriend(id1);
    }

    public void deleteFriends(int id1, int id2) {
        User user1 = userStorage.findById(id1);
        User user2 = userStorage.findById(id2);
        user1.deleteFriend(id2);
        user2.deleteFriend(id1);
    }

    public List<User> getAllFriends(int id) {
        User user = userStorage.findById(id);
        Set<Integer> iDFriends = user.getFriends();
        List<User> userFriends = new ArrayList<>();
        for (Integer n : iDFriends) {
            userFriends.add(userStorage.findById(n));
        }
        return userFriends;
    }

    public List<User> getCommonFriends(int id1, int id2) {
        User user1 = userStorage.findById(id1);
        User user2 = userStorage.findById(id2);

        Set<Integer> friends1 = user1.getFriends();
        Set<Integer> friends2 = user2.getFriends();

        Set<Integer> commonFriends = new HashSet<>(friends1);
        commonFriends.retainAll(friends2);

        if (friends1.isEmpty() || friends2.isEmpty() || commonFriends.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> userFriends = new ArrayList<>();
        for (Integer n : commonFriends) {
            userFriends.add(userStorage.findById(n));
        }
        return userFriends;

    }

}
