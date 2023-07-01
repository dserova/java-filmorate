package ru.yandex.practicum.filorate.dao;

import ru.yandex.practicum.filorate.model.User;

import java.util.List;

public interface UserFriendsDao {

    void createFriends(int id1, int id2);

    void deleteFriends(int id1, int id2);

    List<User> getAllFriends(int id);

    List<User> getCommonFriends(int id1, int id2);

}
