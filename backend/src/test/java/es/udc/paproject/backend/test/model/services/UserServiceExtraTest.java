package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceExtraTest {

    @Autowired
    private UserService userService;

    private User createUser(String userName) {
        return new User(userName, "password", "firstName", "lastName", userName + "@" + userName + ".com");
    }

    @Test
    public void testUpdateProfileCambiaLosDatos() throws InstanceNotFoundException, DuplicateInstanceException {

        User user = createUser("user");
        userService.signUp(user);

        User returnedUser = userService.updateProfile(user.getId(), "newFirstName", "newLastName", "new@user.com");
        User updatedUser = userService.loginFromId(user.getId());

        assertEquals(updatedUser, returnedUser);
        assertEquals("newFirstName", updatedUser.getFirstName());
        assertEquals("newLastName", updatedUser.getLastName());
        assertEquals("new@user.com", updatedUser.getEmail());
        assertEquals("user", updatedUser.getUserName());

    }

    @Test
    public void testSignUpCifraLaPassword() throws DuplicateInstanceException {

        User user = createUser("user");
        userService.signUp(user);

        assertNotEquals("password", user.getPassword());

    }

    @Test
    public void testSignUpIgnoraElRolRecibido() throws DuplicateInstanceException {

        User user = createUser("user");
        user.setRole(User.RoleType.TAQUILLERO);
        userService.signUp(user);

        assertEquals(User.RoleType.ESPECTADOR, user.getRole());

    }

    @Test
    public void testChangePasswordInvalidaLaAnterior() throws Exception {

        User user = createUser("user");
        userService.signUp(user);

        userService.changePassword(user.getId(), "password", "newPassword");

        assertThrows(IncorrectLoginException.class, () -> userService.login("user", "password"));
        assertEquals(user, userService.login("user", "newPassword"));

    }

}
