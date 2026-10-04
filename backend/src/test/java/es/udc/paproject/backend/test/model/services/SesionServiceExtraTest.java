package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.SesionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SesionServiceExtraTest {

    @Autowired
    private SesionService sesionService;

    @Autowired
    private SesionDao sesionDao;

    @Autowired
    private SalaDao salaDao;

    @Autowired
    private PeliculaDao peliculaDao;

    private Sesion crearSesion(LocalDateTime fechaHora) {
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        Pelicula pelicula = peliculaDao.save(new Pelicula("Matrix", "Ciencia ficción", 120));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), 10));
    }

    @Test
    public void testGetDetalleSesionYaComenzada() {
        Sesion sesion = crearSesion(LocalDateTime.now().minusHours(1));

        assertThrows(PastDateException.class, () -> sesionService.getDetalleSesion(sesion.getId()));
    }

    @Test
    public void testGetDetalleSesionDevuelveTodosLosDatos() throws Exception {
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(2).withNano(0);
        Sesion sesion = crearSesion(fechaHora);

        Sesion resultado = sesionService.getDetalleSesion(sesion.getId());

        assertEquals(fechaHora, resultado.getFechaHora());
        assertEquals(10, resultado.getLocalidadesLibres());
        assertEquals(10, resultado.getEntradasDisponibles());
        assertEquals(120, resultado.getPelicula().getDuracion());
        assertEquals("Ciencia ficción", resultado.getPelicula().getResumen());
        assertEquals(100, resultado.getSala().getCapacidad());
    }
}
