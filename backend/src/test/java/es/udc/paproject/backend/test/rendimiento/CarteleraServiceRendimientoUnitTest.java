package es.udc.paproject.backend.test.rendimiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraServiceImpl;

/**
 * RNF-1 a nivel de unidad: coste de la lógica propia de getCartelera
 * (validación de fecha y agrupación por película), con el DAO simulado.
 */
class CarteleraServiceRendimientoUnitTest {

    private static final String TOTAL = "CarteleraService.getCartelera";
    private static final String CONSULTA = "CarteleraService.getCartelera:consulta";
    private static final String AGRUPACION = "CarteleraService.getCartelera:agrupacion";

    private static final int CALENTAMIENTO = 200;
    private static final int REPETICIONES = 1000;
    private static final int SESIONES_POR_PELICULA = 10;
    /** Umbral RNF-1 (unidad): media de la agrupación, en milisegundos. */
    private static final double UMBRAL_AGRUPACION_MS = 5.0;
    /** Umbral RNF-1 (unidad): media del servicio sin base de datos, en milisegundos. */
    private static final double UMBRAL_SERVICIO_MS = 5.0;

    private final LocalDate dia = LocalDate.now().plusDays(1);
    private SesionDao sesionDao;
    private CarteleraServiceImpl carteleraService;

    @BeforeAll
    static void iniciarJetm() {
        MedicionJetm.iniciar();
    }

    @AfterAll
    static void detenerJetm() {
        MedicionJetm.detener();
    }

    @BeforeEach
    void setUp() {
        sesionDao = mock(SesionDao.class);
        carteleraService = new CarteleraServiceImpl();
        ReflectionTestUtils.setField(carteleraService, "sesionDao", sesionDao);
        MedicionJetm.reiniciar();
    }

    private void prepararSesiones(int numeroSesiones) {
        List<Sesion> sesiones = new ArrayList<>();
        Sala sala = new Sala("Sala", 100);
        Pelicula pelicula = null;
        for (int i = 0; i < numeroSesiones; i++) {
            if (i % SESIONES_POR_PELICULA == 0) {
                pelicula = new Pelicula("Pelicula " + i, "Resumen", 120);
            }
            sesiones.add(new Sesion(sala, pelicula, dia.atTime(10, 0).plusMinutes(i),
                    new BigDecimal("7.50"), 100));
        }
        when(sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(any(), any()))
                .thenReturn(sesiones);
    }

    private void medir(int numeroSesiones, String informe) throws Exception {
        prepararSesiones(numeroSesiones);
        for (int i = 0; i < CALENTAMIENTO; i++) {
            carteleraService.getCartelera(dia);
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            carteleraService.getCartelera(dia);
        }
        MedicionJetm.guardarInforme("cartelera-unidad", informe + ": " + numeroSesiones + " sesiones");
    }

    @ParameterizedTest(name = "{0} sesiones")
    @ValueSource(ints = {30, 300, 3000})
    void agrupacionCumpleElUmbralConCargaCreciente(int numeroSesiones) throws Exception {
        medir(numeroSesiones, "agrupacion");

        assertTrue(MedicionJetm.agregado(AGRUPACION).getAverage() < UMBRAL_AGRUPACION_MS);
    }

    @ParameterizedTest(name = "{0} sesiones")
    @ValueSource(ints = {30, 300, 3000})
    void servicioSinBaseDeDatosCumpleElUmbralConCargaCreciente(int numeroSesiones) throws Exception {
        medir(numeroSesiones, "servicio");

        assertTrue(MedicionJetm.agregado(TOTAL).getAverage() < UMBRAL_SERVICIO_MS);
    }

    @Test
    void cadaLlamadaRegistraUnaMedicion() throws Exception {
        prepararSesiones(30);

        for (int i = 0; i < 5; i++) {
            carteleraService.getCartelera(dia);
        }

        assertEquals(5, MedicionJetm.agregado(TOTAL).getMeasurements());
    }

    @Test
    void consultaYAgrupacionSeMidenDentroDelServicio() throws Exception {
        prepararSesiones(30);

        carteleraService.getCartelera(dia);

        assertEquals(Set.of(CONSULTA, AGRUPACION), MedicionJetm.agregado(TOTAL).getChilds().keySet());
    }

    @Test
    void llamadaRechazadaTambienSeMide() throws Exception {
        try {
            carteleraService.getCartelera(LocalDate.now().minusDays(1));
        } catch (PastDateException esperada) {
            // La fecha pasada se rechaza; interesa que el punto de medida se cierre igualmente.
        }

        assertEquals(1, MedicionJetm.agregado(TOTAL).getMeasurements());
    }
}
