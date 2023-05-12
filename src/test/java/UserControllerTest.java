import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filorate.controller.UserController;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {

    @Test
    public void addUser() throws ValidationException {

        UserController userController = new UserController();
        ValidationException thrown;
        User user;

        thrown = assertThrows(ValidationException.class, () -> {
            userController.create(new User(1, "", "login", "name", new Date(50, 1, 1)));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "электронная почта не может быть пустой и должна содержать символ @");

        thrown = assertThrows(ValidationException.class, () -> {
            userController.create(new User(1, "email", "login", "name", new Date(50, 1, 1)));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "электронная почта не может быть пустой и должна содержать символ @");

        thrown = assertThrows(ValidationException.class, () -> {
            userController.create(new User(1, "email@ru", "", "name", new Date(50, 1, 1)));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "логин не может быть пустым и содержать пробелы");

        thrown = assertThrows(ValidationException.class, () -> {
            userController.create(new User(1, "email@ru", "login login", "name", new Date(50, 1, 1)));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "логин не может быть пустым и содержать пробелы");

        user = new User(1, "email@ru", "login", "name", new Date(50, 1, 1));
        assertEquals(userController.create(user), user);

        user = new User(1, "email@ru", "login", "name", new Date(50, 1, 1));
        assertEquals(userController.create(user), user);

        thrown = assertThrows(ValidationException.class, () -> {
            userController.create(new User(1, "email@ru", "login", "name", new Date(2000, 1, 1)));
        });
        assertNotNull(thrown.getMessage());
        assertEquals(thrown.getMessage(), "дата рождения не может быть в будущем.");

    }

}
