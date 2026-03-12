package es.udc.paproject.backend.test.model.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CarteleraServiceTest {

    @Autowired
    private CarteleraService carteleraService;

    @Autowired
    private PeliculaDao peliculaDao;

    @Autowired
    private SalaDao salaDao;

    @Autowired
    private SesionDao sesionDao;

    @Test
    public void testGetCarteleraFechaPasadaLanzaExcepcion() {
        LocalDateTime ayer = LocalDateTime.now().minusDays(1);

        assertThrows(PastDateException.class, () -> {
            carteleraService.getCartelera(ayer);
        });
    }

    @Test
    public void testGetCarteleraCorrecta() throws PastDateException {

        LocalDateTime mañana = LocalDateTime.now().plusDays(1);

        Map<Pelicula, List<Sesion>> cartelera = carteleraService.getCartelera(mañana);
        assertTrue(cartelera != null);
    }

    @Test
    public void testGetCarteleraVerificarOrdenCompleto() throws PastDateException {
        // Setup: Películas con títulos que dictan el orden (A, B)
        Pelicula peliB = new Pelicula("Batman", "El caballero oscuro", 140);
        Pelicula peliA = new Pelicula("Avatar", "Gente azul", 160);
        peliculaDao.save(peliB);
        peliculaDao.save(peliA);

        Sala sala = new Sala("Sala 1", 100);
        salaDao.save(sala);

        LocalDateTime mañana = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        BigDecimal precio = new BigDecimal("9.00");

        // Setup: Sesiones desordenadas cronológicamente para la misma película
        Sesion sTarde = new Sesion(sala, peliA, mañana.withHour(22), precio, 100);
        Sesion sMañana = new Sesion(sala, peliA, mañana.withHour(15), precio, 100);
        Sesion sBatman = new Sesion(sala, peliB, mañana.withHour(12), precio, 100);

        sesionDao.save(sTarde);
        sesionDao.save(sMañana);
        sesionDao.save(sBatman);

        Map<Pelicula, List<Sesion>> cartelera = carteleraService.getCartelera(mañana);

        // VERIFICACIÓN DE ORDEN ALFABÉTICO (Keys)
        List<Pelicula> pelisEnOrden = new ArrayList<>(cartelera.keySet());

        assertEquals("Avatar", pelisEnOrden.get(0).getTitulo(), "La primera debe ser Avatar (A)");
        assertEquals("Batman", pelisEnOrden.get(1).getTitulo(), "La segunda debe ser Batman (B)");

        // VERIFICACIÓN DE ORDEN CRONOLÓGICO (Values)
        List<Sesion> sesionesAvatar = cartelera.get(peliA);

        assertEquals(15, sesionesAvatar.get(0).getFechaHora().getHour(), "La sesión de las 15h debe ir primero");
        assertEquals(22, sesionesAvatar.get(1).getFechaHora().getHour(), "La sesión de las 22h debe ir después");
    }
}