package es.udc.paproject.backend.test.rest.common;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.rest.common.ErrorsDto;
import es.udc.paproject.backend.rest.common.FieldErrorDto;
import es.udc.paproject.backend.rest.common.JwtInfo;

/**
 * Pruebas de cobertura de setters de las clases de datos de rest/common sin lógica propia.
 * No prueban comportamiento: aseguran que cada setter guarda el valor
 * que después devuelve su getter.
 */
class AccesoresCommonUnitTest {

    // ===== ErrorsDto =====

    @Test
    void errorsDtoSetGlobalError() {
        ErrorsDto dto = new ErrorsDto("inicial");
        dto.setGlobalError("error");
        assertEquals("error", dto.getGlobalError());
    }

    @Test
    void errorsDtoSetFieldErrors() {
        ErrorsDto dto = new ErrorsDto("inicial");
        List<FieldErrorDto> errores = List.of(new FieldErrorDto("campo", "mensaje"));
        dto.setFieldErrors(errores);
        assertSame(errores, dto.getFieldErrors());
    }

    // ===== FieldErrorDto =====

    @Test
    void fieldErrorDtoSetFieldName() {
        FieldErrorDto dto = new FieldErrorDto("inicial", "mensaje");
        dto.setFieldName("campo");
        assertEquals("campo", dto.getFieldName());
    }

    @Test
    void fieldErrorDtoSetMessage() {
        FieldErrorDto dto = new FieldErrorDto("campo", "inicial");
        dto.setMessage("mensaje");
        assertEquals("mensaje", dto.getMessage());
    }

    // ===== JwtInfo =====

    @Test
    void jwtInfoSetUserId() {
        JwtInfo info = new JwtInfo(1L, "ESPECTADOR");
        info.setUserId(2L);
        assertEquals(2L, info.getUserId());
    }

    @Test
    void jwtInfoSetRole() {
        JwtInfo info = new JwtInfo(1L, "ESPECTADOR");
        info.setRole("TAQUILLERO");
        assertEquals("TAQUILLERO", info.getRole());
    }
}
