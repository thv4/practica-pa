package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.services.CarteleraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CarteleraServiceExtraTest {

    @Autowired
    private CarteleraService carteleraService;

    @Autowired
    private PeliculaDao peliculaDao;

    @Autowired
    private SalaDao salaDao;

    @Autowired
    private SesionDao sesionDao;

    private Sesion crearSesion(LocalDateTime fechaHora) {
        Pelicula pelicula = peliculaDao.save(new Pelicula("Matrix", "Ciencia ficción", 120));
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("9.00"), 100));
    }

    private List<Sesion> sesiones(Map<Pelicula, List<Sesion>> cartelera) {
        List<Sesion> sesiones = new ArrayList<>();
        cartelera.values().forEach(sesiones::addAll);
        return sesiones;
    }

    @Test
    public void testGetCarteleraSinSesiones() throws Exception {
        assertTrue(carteleraService.getCartelera(LocalDate.now().plusDays(3)).isEmpty());
    }

    @Test
    public void testGetCarteleraLimiteSeisDias() throws Exception {
        LocalDate dia6 = LocalDate.now().plusDays(6);
        Sesion sesion = crearSesion(dia6.atTime(20, 0));

        assertEquals(List.of(sesion), sesiones(carteleraService.getCartelera(dia6)));
        assertThrows(InvalidPost6DaysDateException.class, () -> carteleraService.getCartelera(dia6.plusDays(1)));
    }

    @Test
    public void testGetCarteleraHoyExcluyeSesionesYaComenzadas() throws Exception {
        // Cerca de medianoche no se pueden crear las dos sesiones dentro del mismo día
        LocalTime ahora = LocalTime.now();
        assumeTrue(ahora.isAfter(LocalTime.of(0, 2)) && ahora.isBefore(LocalTime.of(23, 58)));

        LocalDate hoy = LocalDate.now();
        crearSesion(LocalDateTime.now().minusMinutes(1));
        Sesion futura = crearSesion(hoy.atTime(23, 59));

        assertEquals(List.of(futura), sesiones(carteleraService.getCartelera(hoy)));
    }

    @Test
    public void testGetCarteleraSoloSesionesDelDia() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(2);
        crearSesion(dia.minusDays(1).atTime(23, 59));
        Sesion primera = crearSesion(dia.atStartOfDay());
        Sesion ultima = crearSesion(dia.atTime(23, 59, 59));
        crearSesion(dia.plusDays(1).atStartOfDay());

        List<Sesion> result = sesiones(carteleraService.getCartelera(dia));

        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(primera, ultima)));
    }
}
