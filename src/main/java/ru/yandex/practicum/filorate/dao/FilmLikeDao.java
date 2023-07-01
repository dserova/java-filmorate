package ru.yandex.practicum.filorate.dao;

public interface FilmLikeDao {

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

}
