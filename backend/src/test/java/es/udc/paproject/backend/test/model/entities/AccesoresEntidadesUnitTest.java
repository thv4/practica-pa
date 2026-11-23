package es.udc.paproject.backend.test.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.User;

/**
 * Pruebas de cobertura de setters de entidades sin lógica propia.
 * No prueban comportamiento de negocio: aseguran que cada setter guarda
 * el valor que después devuelve su getter.
 */
class AccesoresEntidadesUnitTest {

    // ===== Pelicula =====

    @Test
    void peliculaSetTitulo() {
        Pelicula pelicula = new Pelicula();
        pelicula.setTitulo("Titulo");
        assertEquals("Titulo", pelicula.getTitulo());
    }

    @Test
    void peliculaSetResumen() {
        Pelicula pelicula = new Pelicula();
        pelicula.setResumen("Resumen");
        assertEquals("Resumen", pelicula.getResumen());
    }

    @Test
    void peliculaSetDuracion() {
        Pelicula pelicula = new Pelicula();
        pelicula.setDuracion(95);
        assertEquals(95, pelicula.getDuracion());
    }

    // ===== Sala =====

    @Test
    void salaSetNombre() {
        Sala sala = new Sala();
        sala.setNombre("Sala");
        assertEquals("Sala", sala.getNombre());
    }

    @Test
    void salaSetCapacidad() {
        Sala sala = new Sala();
        sala.setCapacidad(120);
        assertEquals(120, sala.getCapacidad());
    }

    // ===== Sesion =====

    @Test
    void sesionSetSala() {
        Sesion sesion = new Sesion();
        Sala sala = new Sala("Sala", 100);
        sesion.setSala(sala);
        assertSame(sala, sesion.getSala());
    }

    @Test
    void sesionSetPelicula() {
        Sesion sesion = new Sesion();
        Pelicula pelicula = new Pelicula("Titulo", "Resumen", 100);
        sesion.setPelicula(pelicula);
        assertSame(pelicula, sesion.getPelicula());
    }

    @Test
    void sesionSetFechaHora() {
        Sesion sesion = new Sesion();
        LocalDateTime fecha = LocalDateTime.of(2030, 1, 1, 20, 0);
        sesion.setFechaHora(fecha);
        assertEquals(fecha, sesion.getFechaHora());
    }

    @Test
    void sesionSetPrecio() {
        Sesion sesion = new Sesion();
        sesion.setPrecio(new BigDecimal("6.50"));
        assertEquals(new BigDecimal("6.50"), sesion.getPrecio());
    }

    @Test
    void sesionSetVersion() {
        Sesion sesion = new Sesion();
        sesion.setVersion(3L);
        assertEquals(3L, sesion.getVersion());
    }

    // ===== User =====

    @Test
    void userSetUserName() {
        User user = new User();
        user.setUserName("viewer");
        assertEquals("viewer", user.getUserName());
    }
}
