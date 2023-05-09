import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filorate.controller.UserController;
import ru.yandex.practicum.filorate.exception.ValidationException;
import ru.yandex.practicum.filorate.model.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {

    @Test
    public void addUser() throws ValidationException {

        UserController userController = new UserController();

        assertEquals(false, userController.validation(new User(1, "", "login", "name", new Date(50, 1, 1))));
        assertEquals(false, userController.validation(new User(1, "email", "login", "name", new Date(50, 1, 1))));

        assertEquals(false, userController.validation(new User(1, "email@ru", "", "name", new Date(50, 1, 1))));
        assertEquals(false, userController.validation(new User(1, "email", "login login", "name", new Date(50, 1, 1))));
        assertEquals(true, userController.validation(new User(1, "email@ru", "login", "name", new Date(50, 1, 1))));

        assertEquals(false, userController.validation(new User(1, "email@ru", "login", "name", new Date(2000, 1, 1))));
        assertEquals(true, userController.validation(new User(1, "email@ru", "login", "name", new Date(50, 1, 1))));

    }

}
