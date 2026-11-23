package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import es.udc.paproject.backend.model.services.PeliculaService;
import es.udc.paproject.backend.model.services.SesionService;

@SpringBootTest
@ActiveProfiles("test")
class IdentificadorNuloDaoIntegrationTest {

    @Autowired
    private PeliculaService peliculaService;

    @Autowired
    private SesionService sesionService;

    @Test
    void consultarPeliculaConIdentificadorNuloPropagaErrorDeAccesoADatos() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> peliculaService.getDetallePelicula(null));
    }

    @Test
    void consultarSesionConIdentificadorNuloPropagaErrorDeAccesoADatos() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> sesionService.getDetalleSesion(null));
    }
}
