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

public class SesionServiceDynamicTest {

    private SesionDao sesionDao;
    private SesionServiceImpl sesionService;

    @BeforeProperty
    void setUp() {
        sesionDao = Mockito.mock(SesionDao.class);
        when(sesionDao.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        sesionService = new SesionServiceImpl();
        ReflectionTestUtils.setField(sesionService, "sesionDao", sesionDao);
    }

    @Property
    void unaSesionConIdNuloDebeInformarseComoErrorDeEntrada(
            @ForAll("idsNulos") Long sesionId) {
        assertThrows(IllegalArgumentException.class,
                () -> sesionService.getDetalleSesion(sesionId));
    }

    @Provide
    Arbitrary<Long> idsNulos() {
        return Arbitraries.just((Long) null);
    }
}
