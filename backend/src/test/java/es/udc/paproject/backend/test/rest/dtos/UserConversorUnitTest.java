package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.paproject.backend.rest.dtos.UserConversor;
import es.udc.paproject.backend.rest.dtos.UserDto;

class UserConversorUnitTest {

    private static final Long USER_ID = 5L;

    private static User crearUsuario() {
        User user = new User("viewer", "hash", "First", "Last", "viewer@test.com");
        user.setId(USER_ID);
        user.setRole(User.RoleType.TAQUILLERO);
        return user;
    }

    private static UserDto crearUserDto() {
        UserDto dto = new UserDto(null, "viewer", "First", "Last", "viewer@test.com", null);
        dto.setPassword("password");
        return dto;
    }

    // ===== toUserDto =====

    @Test
    void toUserDtoCopiaElIdentificador() {
        assertEquals(USER_ID, UserConversor.toUserDto(crearUsuario()).getId());
    }

    @Test
    void toUserDtoCopiaElNombreDeUsuario() {
        assertEquals("viewer", UserConversor.toUserDto(crearUsuario()).getUserName());
    }

    @Test
    void toUserDtoCopiaElNombre() {
        assertEquals("First", UserConversor.toUserDto(crearUsuario()).getFirstName());
    }

    @Test
    void toUserDtoCopiaLosApellidos() {
        assertEquals("Last", UserConversor.toUserDto(crearUsuario()).getLastName());
    }

    @Test
    void toUserDtoCopiaElEmail() {
        assertEquals("viewer@test.com", UserConversor.toUserDto(crearUsuario()).getEmail());
    }

    @Test
    void toUserDtoExponeElRolComoTexto() {
        assertEquals("TAQUILLERO", UserConversor.toUserDto(crearUsuario()).getRole());
    }

    @Test
    void toUserDtoNoExponeLaContrasena() {
        assertNull(UserConversor.toUserDto(crearUsuario()).getPassword());
    }

    // ===== toUser =====

    @Test
    void toUserCopiaElNombreDeUsuario() {
        assertEquals("viewer", UserConversor.toUser(crearUserDto()).getUserName());
    }

    @Test
    void toUserCopiaLaContrasena() {
        assertEquals("password", UserConversor.toUser(crearUserDto()).getPassword());
    }

    @Test
    void toUserCopiaElNombre() {
        assertEquals("First", UserConversor.toUser(crearUserDto()).getFirstName());
    }

    @Test
    void toUserCopiaLosApellidos() {
        assertEquals("Last", UserConversor.toUser(crearUserDto()).getLastName());
    }

    @Test
    void toUserCopiaElEmail() {
        assertEquals("viewer@test.com", UserConversor.toUser(crearUserDto()).getEmail());
    }

    // ===== toAuthenticatedUserDto =====

    @Test
    void toAuthenticatedUserDtoIncluyeElToken() {
        AuthenticatedUserDto dto = UserConversor.toAuthenticatedUserDto("token", crearUsuario());

        assertEquals("token", dto.getServiceToken());
    }

    @Test
    void toAuthenticatedUserDtoIncluyeElUsuario() {
        AuthenticatedUserDto dto = UserConversor.toAuthenticatedUserDto("token", crearUsuario());

        assertEquals(USER_ID, dto.getUserDto().getId());
    }
}
