package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraServiceImpl;

@ExtendWith(MockitoExtension.class)
class CarteleraServiceImplUnitTest {

    @Mock
    private SesionDao sesionDao;

    @InjectMocks
    private CarteleraServiceImpl carteleraService;

    @Captor
    private ArgumentCaptor<LocalDateTime> inicio;

    @Captor
    private ArgumentCaptor<LocalDateTime> fin;

    private static Sesion crearSesion(Pelicula pelicula, LocalDateTime fechaHora) {
        return new Sesion(new Sala("Sala 1", 100), pelicula, fechaHora, new BigDecimal("7.50"), 100);
    }

    private void daoDevuelve(List<Sesion> sesiones) {
        when(sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(any(), any()))
                .thenReturn(sesiones);
    }

    private void capturarIntervalo() {
        verify(sesionDao).findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(
                inicio.capture(), fin.capture());
    }

    @Test
    void getCarteleraDeAyerLanzaPastDate() {
        assertThrows(PastDateException.class,
                () -> carteleraService.getCartelera(LocalDate.now().minusDays(1)));
    }

    @Test
    void getCarteleraDentroDeSieteDiasLanzaInvalidPost6Days() {
        assertThrows(InvalidPost6DaysDateException.class,
                () -> carteleraService.getCartelera(LocalDate.now().plusDays(7)));
    }

    @Test
    void getCarteleraDeHoyEsAceptada() {
        daoDevuelve(List.of());

        assertDoesNotThrow(() -> carteleraService.getCartelera(LocalDate.now()));
    }

    @Test
    void getCarteleraDentroDeSeisDiasEsAceptada() {
        daoDevuelve(List.of());

        assertDoesNotThrow(() -> carteleraService.getCartelera(LocalDate.now().plusDays(6)));
    }

    @Test
    void getCarteleraDeHoyConsultaDesdeElMomentoActual() throws Exception {
        daoDevuelve(List.of());
        LocalDateTime antes = LocalDateTime.now();

        carteleraService.getCartelera(LocalDate.now());

        capturarIntervalo();
        assertFalse(inicio.getValue().isBefore(antes));
    }

    @Test
    void getCarteleraDeDiaFuturoConsultaDesdeElInicioDelDia() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(2);
        daoDevuelve(List.of());

        carteleraService.getCartelera(dia);

        capturarIntervalo();
        assertEquals(dia.atStartOfDay(), inicio.getValue());
    }

    @Test
    void getCarteleraConsultaHastaAntesDelDiaSiguiente() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(2);
        daoDevuelve(List.of());

        carteleraService.getCartelera(dia);

        capturarIntervalo();
        assertTrue(fin.getValue().isBefore(dia.plusDays(1).atStartOfDay()));
    }

    @Test
    void getCarteleraFinDelDiaSigueEnElMismoDiaAlRedondearASegundos() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(2);
        daoDevuelve(List.of());

        carteleraService.getCartelera(dia);

        capturarIntervalo();
        LocalDateTime redondeado = fin.getValue().plusNanos(500_000_000L).truncatedTo(ChronoUnit.SECONDS);
        assertEquals(dia, redondeado.toLocalDate());
    }

    @Test
    void getCarteleraSinSesionesDevuelveMapaVacio() throws Exception {
        daoDevuelve(List.of());

        assertTrue(carteleraService.getCartelera(LocalDate.now().plusDays(1)).isEmpty());
    }

    @Test
    void getCarteleraAgrupaLasSesionesDeLaMismaPelicula() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(1);
        Pelicula pelicula = new Pelicula("A", "Resumen", 100);
        daoDevuelve(List.of(crearSesion(pelicula, dia.atTime(17, 0)),
                crearSesion(pelicula, dia.atTime(20, 0))));

        Map<Pelicula, List<Sesion>> cartelera = carteleraService.getCartelera(dia);

        assertEquals(2, cartelera.get(pelicula).size());
    }

    @Test
    void getCarteleraConservaElOrdenDePeliculasDelDao() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(1);
        Pelicula primera = new Pelicula("A", "Resumen", 100);
        Pelicula segunda = new Pelicula("B", "Resumen", 100);
        daoDevuelve(List.of(crearSesion(primera, dia.atTime(22, 0)),
                crearSesion(segunda, dia.atTime(16, 0))));

        Map<Pelicula, List<Sesion>> cartelera = carteleraService.getCartelera(dia);

        assertEquals(List.of(primera, segunda), new ArrayList<>(cartelera.keySet()));
    }

    @Test
    void getCarteleraConservaElOrdenDeSesionesDentroDeLaPelicula() throws Exception {
        LocalDate dia = LocalDate.now().plusDays(1);
        Pelicula pelicula = new Pelicula("A", "Resumen", 100);
        Sesion primera = crearSesion(pelicula, dia.atTime(17, 0));
        Sesion segunda = crearSesion(pelicula, dia.atTime(20, 0));
        daoDevuelve(List.of(primera, segunda));

        Map<Pelicula, List<Sesion>> cartelera = carteleraService.getCartelera(dia);

        assertEquals(List.of(primera, segunda), cartelera.get(pelicula));
    }
}
