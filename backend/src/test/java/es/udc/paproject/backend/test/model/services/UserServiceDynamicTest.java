package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.paproject.backend.model.services.PermissionChecker;
import es.udc.paproject.backend.model.services.UserServiceImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class UserServiceDynamicTest {

    private UserServiceImpl crearServicio(UserDao userDao, BCryptPasswordEncoder passwordEncoder) {
        UserServiceImpl servicio = new UserServiceImpl();
        ReflectionTestUtils.setField(servicio, "userDao", userDao);
        ReflectionTestUtils.setField(servicio, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(servicio, "permissionChecker", mock(PermissionChecker.class));
        return servicio;
    }

    private User nuevoUsuario(String nombre, String contrasena) {
        return new User(nombre, contrasena, "Nombre", "Apellido", "viewer@example.com");
    }

    @Example
    void registrarseConUsuarioNuloDebeInformarseComoErrorDeEntrada() {
        UserServiceImpl servicio = crearServicio(mock(UserDao.class), mock(BCryptPasswordEncoder.class));

        assertThrows(IllegalArgumentException.class, () -> servicio.signUp(null));
    }

    @Property(tries = 100)
    void registroGuardaLaContrasenaCodificada(@ForAll("textosUsuario") String nombre,
            @ForAll("textosUsuario") String contrasena) throws Exception {
        UserDao userDao = mock(UserDao.class);
        BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
        when(encoder.encode(contrasena)).thenReturn("hash:" + contrasena);
        UserServiceImpl servicio = crearServicio(userDao, encoder);
        User usuario = nuevoUsuario(nombre, contrasena);

        servicio.signUp(usuario);

        assertEquals("hash:" + contrasena, usuario.getPassword());
    }

    @Property(tries = 100)
    void registroAsignaRolEspectador(@ForAll("textosUsuario") String nombre,
            @ForAll User.RoleType rolInicial) throws Exception {
        UserServiceImpl servicio = crearServicio(mock(UserDao.class), mock(BCryptPasswordEncoder.class));
        User usuario = nuevoUsuario(nombre, "contrasena");
        usuario.setRole(rolInicial);

        servicio.signUp(usuario);

        assertEquals(User.RoleType.ESPECTADOR, usuario.getRole());
    }

    @Property(tries = 100)
    void registroConNombreDuplicadoLanzaExcepcion(@ForAll("textosUsuario") String nombre) {
        UserDao userDao = mock(UserDao.class);
        when(userDao.existsByUserName(anyString())).thenReturn(true);
        UserServiceImpl servicio = crearServicio(userDao, mock(BCryptPasswordEncoder.class));
        User usuario = nuevoUsuario(nombre, "contrasena");

        assertThrows(DuplicateInstanceException.class, () -> servicio.signUp(usuario));
    }

    @Provide
    Arbitrary<String> textosUsuario() {
        return Arbitraries.strings().alpha().numeric().ofMinLength(1).ofMaxLength(30);
    }
}
