package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.SesionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class SesionServiceTest {

    @Autowired
    private SesionService sesionService;

    @Autowired
    private SesionDao sesionDao;

    @Autowired
    private SalaDao salaDao;

    @Autowired
    private PeliculaDao peliculaDao;

    @Test
    public void testGetDetalleSesion() throws Exception {

        // Crear Sala
        Sala sala = new Sala("Sala 1", 100);
        salaDao.save(sala);

        // Crear Pelicula
        Pelicula pelicula = new Pelicula("Matrix",
                "Ciencia ficción",
                120);
        peliculaDao.save(pelicula);


        Sesion sesion = new Sesion(
                sala,
                pelicula,
                LocalDateTime.now().plusDays(1),
                new BigDecimal("7.50"),
                10
        );

        sesionDao.save(sesion);

        // Recuperar usando el servicio
        Sesion resultado = sesionService.getDetalleSesion(sesion.getId());

        // Comprobaciones
        assertNotNull(resultado);
        assertEquals(sesion.getId(), resultado.getId());
        assertEquals("Matrix", resultado.getPelicula().getTitulo());
        assertEquals("Sala 1", resultado.getSala().getNombre());
        assertEquals(new BigDecimal("7.50"), resultado.getPrecio());
    }
    @Test
    public void testGetDetalleSesionNoExiste() {

        assertThrows(InstanceNotFoundException.class, () -> {
            sesionService.getDetalleSesion(-1L);
        });
    }
}