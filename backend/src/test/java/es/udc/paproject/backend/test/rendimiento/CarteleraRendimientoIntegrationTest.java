package es.udc.paproject.backend.test.rendimiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.SalaDao;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.services.CarteleraService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * RNF-1 a nivel de integración: getCartelera con el DAO real y MySQL,
 * para volúmenes crecientes de sesiones en un mismo día.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CarteleraRendimientoIntegrationTest {

    private static final String TOTAL = "CarteleraService.getCartelera";

    private static final int CALENTAMIENTO = 10;
    private static final int REPETICIONES = 50;
    private static final int SESIONES_POR_PELICULA = 10;
    private static final int SALAS = 5;
    /** Umbral RNF-1 (integración): media de getCartelera, en milisegundos. */
    private static final double UMBRAL_CARTELERA_MS = 50.0;

    private final LocalDate dia = LocalDate.now().plusDays(2);

    @Autowired
    private CarteleraService carteleraService;
    @Autowired
    private SesionDao sesionDao;
    @Autowired
    private SalaDao salaDao;
    @Autowired
    private PeliculaDao peliculaDao;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

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
        MedicionJetm.reiniciar();
    }

    /** Crea las sesiones del día y vacía el contexto de persistencia para que las consultas lleguen a MySQL. */
    private void crearSesiones(int numeroSesiones) {
        List<Sala> salas = new ArrayList<>();
        for (int i = 0; i < SALAS; i++) {
            salas.add(salaDao.save(new Sala("Sala " + i, 100)));
        }
        Pelicula pelicula = null;
        for (int i = 0; i < numeroSesiones; i++) {
            if (i % SESIONES_POR_PELICULA == 0) {
                pelicula = peliculaDao.save(new Pelicula("Rendimiento " + i, "Resumen", 120));
            }
            sesionDao.save(new Sesion(salas.get(i % SALAS), pelicula, dia.atTime(10, 0).plusMinutes(i),
                    new BigDecimal("7.50"), 100));
        }
        entityManager.flush();
        entityManager.clear();
    }

    private void llamar() throws Exception {
        carteleraService.getCartelera(dia);
        entityManager.clear();
    }

    private long sentenciasSqlDeUnaLlamada() throws Exception {
        Statistics estadisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        estadisticas.setStatisticsEnabled(true);
        estadisticas.clear();
        llamar();
        return estadisticas.getPrepareStatementCount();
    }

    @ParameterizedTest(name = "{0} sesiones")
    @ValueSource(ints = {30, 300})
    void carteleraCumpleElUmbralConCargaCreciente(int numeroSesiones) throws Exception {
        crearSesiones(numeroSesiones);
        for (int i = 0; i < CALENTAMIENTO; i++) {
            llamar();
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            llamar();
        }
        MedicionJetm.guardarInforme("cartelera-integracion", numeroSesiones + " sesiones");

        assertTrue(MedicionJetm.agregado(TOTAL).getAverage() < UMBRAL_CARTELERA_MS);
    }

    /** RNF-1b: el número de sentencias SQL no debe crecer con el número de películas del día. */
    @Test
    void sentenciasSqlNoCrecenConElNumeroDePeliculas() throws Exception {
        crearSesiones(30);
        long conTresPeliculas = sentenciasSqlDeUnaLlamada();
        crearSesiones(300);
        long conTreintaYTresPeliculas = sentenciasSqlDeUnaLlamada();

        assertEquals(conTresPeliculas, conTreintaYTresPeliculas);
    }
}
