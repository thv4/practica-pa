package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PeliculaServiceImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class PeliculaServiceDynamicTest {

    private PeliculaServiceImpl servicioCon(PeliculaDao dao) {
        PeliculaServiceImpl servicio = new PeliculaServiceImpl();
        ReflectionTestUtils.setField(servicio, "peliculaDao", dao);
        return servicio;
    }

    @Property
    void unaPeliculaExistenteSeDevuelveParaCualquierIdPositivo(
            @ForAll("idsPositivos") long id) throws Exception {
        PeliculaDao dao = mock(PeliculaDao.class);
        Pelicula pelicula = mock(Pelicula.class);
        when(dao.findById(id)).thenReturn(Optional.of(pelicula));

        assertSame(pelicula, servicioCon(dao).getDetallePelicula(id));
    }

    @Property
    void unaPeliculaInexistenteLanzaInstanceNotFoundParaCualquierIdPositivo(
            @ForAll("idsPositivos") long id) {
        PeliculaDao dao = mock(PeliculaDao.class);
        when(dao.findById(id)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> servicioCon(dao).getDetallePelicula(id));
    }

    @Example
    void unErrorDelDaoParaIdNuloSePropaga() {
        PeliculaDao dao = mock(PeliculaDao.class);
        when(dao.findById(null)).thenThrow(new IllegalArgumentException("id nulo"));

        assertThrows(IllegalArgumentException.class, () -> servicioCon(dao).getDetallePelicula(null));
    }

    @Example
    void unDaoSimuladoQueDevuelveVacioParaIdNuloProduceInstanceNotFound() {
        PeliculaDao dao = mock(PeliculaDao.class);
        when(dao.findById(null)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class, () -> servicioCon(dao).getDetallePelicula(null));
    }

    @Provide
    Arbitrary<Long> idsPositivos() {
        return Arbitraries.longs().between(1L, Long.MAX_VALUE);
    }
}
