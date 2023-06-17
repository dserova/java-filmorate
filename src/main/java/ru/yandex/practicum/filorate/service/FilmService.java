package ru.yandex.practicum.filorate.service;

public interface FilmService {

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

}
