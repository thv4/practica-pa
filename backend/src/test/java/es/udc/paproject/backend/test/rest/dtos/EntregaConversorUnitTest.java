package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.rest.dtos.EntregaConversor;

class EntregaConversorUnitTest {

    @Test
    void toSuccessResultSinMensajeEsExitoso() {
        assertTrue(EntregaConversor.toSuccessResult().isSuccess());
    }

    @Test
    void toSuccessResultSinMensajeUsaElMensajePorDefecto() {
        assertEquals("Entregas realizadas correctamente", EntregaConversor.toSuccessResult().getMensaje());
    }

    @Test
    void toSuccessResultConMensajeEsExitoso() {
        assertTrue(EntregaConversor.toSuccessResult("ok").isSuccess());
    }

    @Test
    void toSuccessResultConMensajeConservaElMensaje() {
        assertEquals("ok", EntregaConversor.toSuccessResult("ok").getMensaje());
    }

    @Test
    void toErrorResultNoEsExitoso() {
        assertFalse(EntregaConversor.toErrorResult("error").isSuccess());
    }

    @Test
    void toErrorResultConservaElMensaje() {
        assertEquals("error", EntregaConversor.toErrorResult("error").getMensaje());
    }
}
