package es.udc.paproject.backend.test.model.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.exceptions.IncorrectLoginException;
import es.udc.paproject.backend.model.exceptions.InstanceException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;

class ExceptionsUnitTest {

    @Test
    void pastDateExceptionConservaLaFecha() {
        assertEquals("2020-01-01", new PastDateException("2020-01-01").getDate());
    }

    @Test
    void pastDateExceptionIncluyeLaFechaEnElMensaje() {
        assertEquals("Date is in the past => 2020-01-01", new PastDateException("2020-01-01").getMessage());
    }

    @Test
    void incorrectLoginExceptionConservaElNombreDeUsuario() {
        assertEquals("viewer", new IncorrectLoginException("viewer", "secret").getUserName());
    }

    @Test
    void incorrectLoginExceptionConservaLaContrasena() {
        assertEquals("secret", new IncorrectLoginException("viewer", "secret").getPassword());
    }

    @Test
    void instanceNotFoundExceptionConservaElNombreDeLaEntidad() {
        assertEquals("project.entities.user", new InstanceNotFoundException("project.entities.user", 5L).getName());
    }

    @Test
    void instanceNotFoundExceptionConservaLaClave() {
        assertEquals(5L, new InstanceNotFoundException("project.entities.user", 5L).getKey());
    }

    @Test
    void instanceExceptionConMensajeConservaElMensaje() {
        InstanceException exception = new InstanceException("mensaje") {};

        assertEquals("mensaje", exception.getMessage());
    }
}
