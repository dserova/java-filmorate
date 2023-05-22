package ru.yandex.practicum.filorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filorate.service.FilmService;
import ru.yandex.practicum.filorate.service.UserService;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);
        UserService userService = context.getBean(UserService.class);
        FilmService filmService = context.getBean(FilmService.class);
    }

}
