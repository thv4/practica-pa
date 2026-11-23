package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.exceptions.IncorrectPasswordException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PermissionChecker;
import es.udc.paproject.backend.model.services.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "viewer";
    private static final String PASSWORD = "password";
    private static final String HASH = "hash-password";

    @Mock
    private PermissionChecker permissionChecker;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(USER_NAME, PASSWORD, "First", "Last", "viewer@test.com");
        user.setId(USER_ID);
    }

    private User usuarioRegistrado() {
        User registrado = new User(USER_NAME, HASH, "First", "Last", "viewer@test.com");
        registrado.setId(USER_ID);
        return registrado;
    }

    private InstanceNotFoundException usuarioNoEncontrado() {
        return new InstanceNotFoundException("project.entities.user", USER_ID);
    }

    @Test
    void signUpConNombreRepetidoLanzaDuplicateInstance() {
        when(userDao.existsByUserName(USER_NAME)).thenReturn(true);

        assertThrows(DuplicateInstanceException.class, () -> userService.signUp(user));
    }

    @Test
    void signUpConNombreRepetidoNoGuardaElUsuario() {
        when(userDao.existsByUserName(USER_NAME)).thenReturn(true);

        try {
            userService.signUp(user);
        } catch (DuplicateInstanceException expected) {
            // se comprueba el efecto, no la excepción
        }

        verify(userDao, never()).save(any());
    }

    @Test
    void signUpGuardaLaContrasenaCodificada() throws Exception {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(HASH);

        userService.signUp(user);

        assertEquals(HASH, user.getPassword());
    }

    @Test
    void signUpAsignaSiempreElRolEspectador() throws Exception {
        user.setRole(User.RoleType.TAQUILLERO);

        userService.signUp(user);

        assertEquals(User.RoleType.ESPECTADOR, user.getRole());
    }

    @Test
    void signUpGuardaElUsuario() throws Exception {
        userService.signUp(user);

        verify(userDao).save(user);
    }

    @Test
    void loginCorrectoDevuelveElUsuario() throws Exception {
        User registrado = usuarioRegistrado();
        when(userDao.findByUserName(USER_NAME)).thenReturn(Optional.of(registrado));
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);

        assertSame(registrado, userService.login(USER_NAME, PASSWORD));
    }

    @Test
    void loginConUsuarioInexistenteLanzaIncorrectLogin() {
        when(userDao.findByUserName(USER_NAME)).thenReturn(Optional.empty());

        assertThrows(IncorrectLoginException.class, () -> userService.login(USER_NAME, PASSWORD));
    }

    @Test
    void loginConContrasenaIncorrectaLanzaIncorrectLogin() {
        when(userDao.findByUserName(USER_NAME)).thenReturn(Optional.of(usuarioRegistrado()));
        when(passwordEncoder.matches("otra", HASH)).thenReturn(false);

        assertThrows(IncorrectLoginException.class, () -> userService.login(USER_NAME, "otra"));
    }

    @Test
    void loginFromIdExistenteDevuelveElUsuario() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        assertSame(user, userService.loginFromId(USER_ID));
    }

    @Test
    void loginFromIdInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenThrow(usuarioNoEncontrado());

        assertThrows(InstanceNotFoundException.class, () -> userService.loginFromId(USER_ID));
    }

    @Test
    void updateProfileCambiaElNombre() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        User actualizado = userService.updateProfile(USER_ID, "Nuevo", "Apellido", "nuevo@test.com");

        assertEquals("Nuevo", actualizado.getFirstName());
    }

    @Test
    void updateProfileCambiaLosApellidos() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        User actualizado = userService.updateProfile(USER_ID, "Nuevo", "Apellido", "nuevo@test.com");

        assertEquals("Apellido", actualizado.getLastName());
    }

    @Test
    void updateProfileCambiaElEmail() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        User actualizado = userService.updateProfile(USER_ID, "Nuevo", "Apellido", "nuevo@test.com");

        assertEquals("nuevo@test.com", actualizado.getEmail());
    }

    @Test
    void updateProfileNoCambiaElNombreDeUsuario() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        User actualizado = userService.updateProfile(USER_ID, "Nuevo", "Apellido", "nuevo@test.com");

        assertEquals(USER_NAME, actualizado.getUserName());
    }

    @Test
    void updateProfileDeUsuarioInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenThrow(usuarioNoEncontrado());

        assertThrows(InstanceNotFoundException.class,
                () -> userService.updateProfile(USER_ID, "Nuevo", "Apellido", "nuevo@test.com"));
    }

    @Test
    void changePasswordCorrectaGuardaLaNuevaCodificada() throws Exception {
        User registrado = usuarioRegistrado();
        when(permissionChecker.checkUser(USER_ID)).thenReturn(registrado);
        when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
        when(passwordEncoder.encode("nueva")).thenReturn("hash-nueva");

        userService.changePassword(USER_ID, PASSWORD, "nueva");

        assertEquals("hash-nueva", registrado.getPassword());
    }

    @Test
    void changePasswordConAntiguaIncorrectaLanzaIncorrectPassword() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(usuarioRegistrado());
        when(passwordEncoder.matches("otra", HASH)).thenReturn(false);

        assertThrows(IncorrectPasswordException.class,
                () -> userService.changePassword(USER_ID, "otra", "nueva"));
    }

    @Test
    void changePasswordConAntiguaIncorrectaMantieneLaContrasena() throws Exception {
        User registrado = usuarioRegistrado();
        when(permissionChecker.checkUser(USER_ID)).thenReturn(registrado);
        when(passwordEncoder.matches("otra", HASH)).thenReturn(false);

        try {
            userService.changePassword(USER_ID, "otra", "nueva");
        } catch (IncorrectPasswordException expected) {
            // se comprueba el efecto, no la excepción
        }

        assertEquals(HASH, registrado.getPassword());
    }

    @Test
    void changePasswordDeUsuarioInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenThrow(usuarioNoEncontrado());

        assertThrows(InstanceNotFoundException.class,
                () -> userService.changePassword(USER_ID, PASSWORD, "nueva"));
    }
}
