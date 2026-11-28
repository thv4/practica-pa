package es.udc.paproject.backend.test.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;

class SesionUnitTest {

    private static final int MUCHAS_LOCALIDADES = 100;

    private static Sesion crearSesion(LocalDateTime fechaHora, int localidadesLibres) {
        return new Sesion(new Sala("Sala 1", MUCHAS_LOCALIDADES),
                new Pelicula("Pelicula", "Resumen", 120),
                fechaHora, new BigDecimal("7.50"), localidadesLibres);
    }

    private static Sesion crearSesionFutura(int localidadesLibres) {
        return crearSesion(LocalDateTime.now().plusDays(1), localidadesLibres);
    }

    @ParameterizedTest(name = "{0} entradas con stock suficiente")
    @ValueSource(ints = {1, 5, 10})
    void hayDisponibilidadDentroDelRangoPermitidoEsCierto(int numeroEntradas) {
        assertTrue(crearSesionFutura(MUCHAS_LOCALIDADES).hayDisponibilidad(numeroEntradas));
    }

    @ParameterizedTest(name = "{0} entradas fuera de rango")
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 11, Integer.MAX_VALUE})
    void hayDisponibilidadFueraDelRangoPermitidoEsFalso(int numeroEntradas) {
        assertFalse(crearSesionFutura(MUCHAS_LOCALIDADES).hayDisponibilidad(numeroEntradas));
    }

    @Test
    void hayDisponibilidadPidiendoExactamenteLasLibresEsCierto() {
        assertTrue(crearSesionFutura(5).hayDisponibilidad(5));
    }

    @Test
    void hayDisponibilidadPidiendoUnaMasQueLasLibresEsFalso() {
        assertFalse(crearSesionFutura(5).hayDisponibilidad(6));
    }

    @Test
    void hayDisponibilidadSinLocalidadesLibresEsFalso() {
        assertFalse(crearSesionFutura(0).hayDisponibilidad(1));
    }

    @Test
    void getEntradasDisponiblesDevuelveLasLocalidadesLibres() {
        assertEquals(42, crearSesionFutura(42).getEntradasDisponibles());
    }

    @Test
    void getVersionDevuelveLaVersionAsignada() {
        Sesion sesion = crearSesionFutura(10);
        sesion.setVersion(7L);

        assertEquals(7L, sesion.getVersion());
    }

    @Test
    void haComenzadoEnSesionPasadaEsCierto() {
        assertTrue(crearSesion(LocalDateTime.now().minusMinutes(1), 10).haComenzado());
    }

    @Test
    void haComenzadoEnSesionFuturaEsFalso() {
        assertFalse(crearSesion(LocalDateTime.now().plusHours(1), 10).haComenzado());
    }
}
