package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.*;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.services.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

public class UserServiceDynamicTest {

    private UserServiceImpl userService;

    @BeforeProperty
    void setUp() {
        userService = new UserServiceImpl();
    }

    @Property
    void registrarseConUsuarioNuloDebeInformarseComoErrorDeEntrada(
            @ForAll("usuariosNulos") User user) {
        assertThrows(IllegalArgumentException.class,
                () -> userService.signUp(user));
    }

    @Provide
    Arbitrary<User> usuariosNulos() {
        return Arbitraries.just((User) null);
    }
}
