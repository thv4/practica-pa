package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.rest.dtos.BuyParamsDto;
import es.udc.paproject.backend.rest.dtos.EntregarEntradasParamsDto;
import es.udc.paproject.backend.rest.dtos.UserDto;

/**
 * DTOs que reciben datos del cliente: comprueba que conservan lo recibido
 * y el tratamiento de valores nulos y espacios en UserDto.
 */
class ParamsDtoUnitTest {

    private static final String TARJETA = "1234567890123456";

    // ===== BuyParamsDto =====

    @Test
    void buyParamsDtoConservaLaSesion() {
        assertEquals(3L, new BuyParamsDto(3L, 2, TARJETA).getSesionId());
    }

    @Test
    void buyParamsDtoConservaElNumeroDeLocalidades() {
        assertEquals(2, new BuyParamsDto(3L, 2, TARJETA).getNumLocalidades());
    }

    @Test
    void buyParamsDtoConservaLaTarjeta() {
        assertEquals(TARJETA, new BuyParamsDto(3L, 2, TARJETA).getTarjetaBancaria());
    }

    // ===== EntregarEntradasParamsDto =====

    @Test
    void entregarEntradasParamsDtoConservaLaCompra() {
        assertEquals(7L, new EntregarEntradasParamsDto(7L, TARJETA).getCompraId());
    }

    @Test
    void entregarEntradasParamsDtoConservaLaTarjeta() {
        assertEquals(TARJETA, new EntregarEntradasParamsDto(7L, TARJETA).getTarjetaBancaria());
    }

    // ===== UserDto =====

    @Test
    void userDtoConNombreDeUsuarioNuloLoMantieneNulo() {
        assertNull(new UserDto(1L, null, "First", "Last", "a@test.com", "ESPECTADOR").getUserName());
    }

    @Test
    void userDtoEliminaEspaciosDelNombreDeUsuario() {
        assertEquals("viewer",
                new UserDto(1L, "  viewer  ", "First", "Last", "a@test.com", "ESPECTADOR").getUserName());
    }

    @Test
    void userDtoEliminaEspaciosDelEmail() {
        assertEquals("a@test.com",
                new UserDto(1L, "viewer", "First", "Last", " a@test.com ", "ESPECTADOR").getEmail());
    }

    @Test
    void userDtoSetIdCambiaElIdentificador() {
        UserDto dto = new UserDto();
        dto.setId(9L);

        assertEquals(9L, dto.getId());
    }

    @Test
    void userDtoSetRoleCambiaElRol() {
        UserDto dto = new UserDto();
        dto.setRole("TAQUILLERO");

        assertEquals("TAQUILLERO", dto.getRole());
    }
}
