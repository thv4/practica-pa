package es.udc.paproject.backend.test.model.services;


import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PeliculaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PeliculaServiceTest {

    private final Long NON_EXISTENT_ID = Long.valueOf(-1L);

    @Autowired
    private PeliculaDao peliculaDao;

    @Autowired
    private PeliculaService peliculaService;

    private Pelicula createPelicula(String nombre, String resumen, int duracion) {
        return new Pelicula(nombre, resumen, duracion);
    }

    @Test
    public void testGetDetallePelicula()  throws InstanceNotFoundException {
        Pelicula pelicula = createPelicula("Robocop", "policia robot", 120);

        peliculaDao.save(pelicula);

        assertEquals(pelicula,  peliculaService.getDetallePelicula(pelicula.getId()));

    }

    @Test
    public void testGetDetallePeliculaNonExistent() {
        assertThrows(InstanceNotFoundException.class, () -> peliculaService.getDetallePelicula(NON_EXISTENT_ID));
    }

}
