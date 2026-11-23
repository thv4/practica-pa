package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.SesionServiceImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class SesionServiceDynamicTest {

    private SesionServiceImpl servicioCon(SesionDao dao) {
        SesionServiceImpl servicio = new SesionServiceImpl();
        ReflectionTestUtils.setField(servicio, "sesionDao", dao);
        return servicio;
    }

    @Property
    void unaSesionFuturaSeDevuelveParaCualquierIdPositivo(
            @ForAll("idsPositivos") long id, @ForAll("dias") int dias) throws Exception {
        SesionDao dao = mock(SesionDao.class);
        Sesion sesion = mock(Sesion.class);
        when(sesion.getFechaHora()).thenReturn(LocalDateTime.now().plusDays(dias));
        when(dao.findById(id)).thenReturn(Optional.of(sesion));

        assertSame(sesion, servicioCon(dao).getDetalleSesion(id));
    }

    @Property
    void unaSesionPasadaLanzaPastDateParaCualquierIdPositivo(
            @ForAll("idsPositivos") long id, @ForAll("dias") int dias) {
        SesionDao dao = mock(SesionDao.class);
        Sesion sesion = mock(Sesion.class);
        when(sesion.getFechaHora()).thenReturn(LocalDateTime.now().minusDays(dias));
        when(dao.findById(id)).thenReturn(Optional.of(sesion));

        assertThrows(PastDateException.class, () -> servicioCon(dao).getDetalleSesion(id));
    }

    @Property
    void unaSesionInexistenteLanzaInstanceNotFoundParaCualquierIdPositivo(
            @ForAll("idsPositivos") long id) {
        SesionDao dao = mock(SesionDao.class);
        when(dao.findById(id)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> servicioCon(dao).getDetalleSesion(id));
    }

    @Example
    void unErrorDelDaoParaIdNuloSePropaga() {
        SesionDao dao = mock(SesionDao.class);
        when(dao.findById(null)).thenThrow(new IllegalArgumentException("id nulo"));

        assertThrows(IllegalArgumentException.class, () -> servicioCon(dao).getDetalleSesion(null));
    }

    @Example
    void unDaoSimuladoQueDevuelveVacioParaIdNuloProduceInstanceNotFound() {
        SesionDao dao = mock(SesionDao.class);
        when(dao.findById(null)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> servicioCon(dao).getDetalleSesion(null));
    }

    @Provide
    Arbitrary<Long> idsPositivos() {
        return Arbitraries.longs().between(1L, Long.MAX_VALUE);
    }

    @Provide
    Arbitrary<Integer> dias() {
        return Arbitraries.integers().between(1, 365);
    }
}
