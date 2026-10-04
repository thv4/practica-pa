package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.*;
import java.util.*;

import org.mockito.*;
import org.springframework.test.util.*;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.services.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

public class CarteleraServiceDynamicTest {

    private SesionDao sesionDao;
    private CarteleraServiceImpl carteleraService;

    @BeforeProperty
    void setUp() {
        sesionDao = Mockito.mock(SesionDao.class);
        when(sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(
                Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        carteleraService = new CarteleraServiceImpl();
        ReflectionTestUtils.setField(carteleraService, "sesionDao", sesionDao);
    }

    @Property
    void unaFechaNulaDebeInformarseComoErrorDeEntrada(
            @ForAll("fechasNulas") LocalDate dia) {
        assertThrows(IllegalArgumentException.class,
                () -> carteleraService.getCartelera(dia));
    }

    @Provide
    Arbitrary<LocalDate> fechasNulas() {
        return Arbitraries.just((LocalDate) null);
    }
}
