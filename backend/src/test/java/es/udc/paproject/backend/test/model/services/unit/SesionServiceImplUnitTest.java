package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.SesionServiceImpl;

@ExtendWith(MockitoExtension.class)
class SesionServiceImplUnitTest {

    private static final Long SESION_ID = 1L;

    @Mock
    private SesionDao sesionDao;

    @InjectMocks
    private SesionServiceImpl sesionService;

    private static Sesion crearSesion(LocalDateTime fechaHora) {
        return new Sesion(new Sala("Sala 1", 100), new Pelicula("Pelicula", "Resumen", 120),
                fechaHora, new BigDecimal("7.50"), 100);
    }

    @Test
    void getDetalleSesionFuturaDevuelveLaSesion() throws Exception {
        Sesion sesion = crearSesion(LocalDateTime.now().plusDays(1));
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesion));

        assertSame(sesion, sesionService.getDetalleSesion(SESION_ID));
    }

    @Test
    void getDetalleSesionInexistenteLanzaInstanceNotFound() {
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> sesionService.getDetalleSesion(SESION_ID));
    }

    @Test
    void getDetalleSesionPasadaLanzaPastDate() {
        when(sesionDao.findById(SESION_ID))
                .thenReturn(Optional.of(crearSesion(LocalDateTime.now().minusMinutes(1))));

        assertThrows(PastDateException.class, () -> sesionService.getDetalleSesion(SESION_ID));
    }
}
