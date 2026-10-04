package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.mockito.*;
import org.springframework.test.util.*;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.services.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

public class PeliculaServiceDynamicTest {

    private PeliculaDao peliculaDao;
    private PeliculaServiceImpl peliculaService;

    @BeforeProperty
    void setUp() {
        peliculaDao = Mockito.mock(PeliculaDao.class);
        when(peliculaDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        peliculaService = new PeliculaServiceImpl();
        ReflectionTestUtils.setField(peliculaService, "peliculaDao", peliculaDao);
    }

    @Property
    void unaPeliculaConIdNuloDebeInformarseComoErrorDeEntrada(
            @ForAll("idsNulos") Long peliculaId) {
        assertThrows(IllegalArgumentException.class,
                () -> peliculaService.getDetallePelicula(peliculaId));
    }

    @Provide
    Arbitrary<Long> idsNulos() {
        return Arbitraries.just((Long) null);
    }
}
