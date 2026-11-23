package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PeliculaServiceImpl;

@ExtendWith(MockitoExtension.class)
class PeliculaServiceImplUnitTest {

    private static final Long PELICULA_ID = 1L;

    @Mock
    private PeliculaDao peliculaDao;

    @InjectMocks
    private PeliculaServiceImpl peliculaService;

    @Test
    void getDetallePeliculaExistenteDevuelveLaPelicula() throws Exception {
        Pelicula pelicula = new Pelicula("Pelicula", "Resumen", 120);
        when(peliculaDao.findById(PELICULA_ID)).thenReturn(Optional.of(pelicula));

        assertSame(pelicula, peliculaService.getDetallePelicula(PELICULA_ID));
    }

    @Test
    void getDetallePeliculaInexistenteLanzaInstanceNotFound() {
        when(peliculaDao.findById(PELICULA_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class,
                () -> peliculaService.getDetallePelicula(PELICULA_ID));
    }
}
