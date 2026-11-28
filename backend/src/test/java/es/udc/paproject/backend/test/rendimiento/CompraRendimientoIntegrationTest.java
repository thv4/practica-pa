package es.udc.paproject.backend.test.rendimiento;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.SalaDao;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.model.services.CompraService;
import jakarta.persistence.EntityManager;

/**
 * RNF-2 y RNF-3 a nivel de integración: historial con muchas compras y compra de
 * entradas, con los DAOs reales y MySQL.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CompraRendimientoIntegrationTest {

    private static final String HISTORICO = "CompraService.findHistoricoCompras";
    private static final String COMPRAR = "CompraService.comprarEntradas";

    private static final String TARJETA = "1234567890123456";
    private static final int CALENTAMIENTO = 10;
    private static final int REPETICIONES = 50;
    private static final int COMPRAS_DEL_USUARIO = 1000;
    private static final int TAMANO_PAGINA = 10;
    /** Umbral RNF-2 (integración): media de una página del historial, en milisegundos. */
    private static final double UMBRAL_HISTORICO_MS = 30.0;
    /** Umbral RNF-3 (integración): media de comprarEntradas, en milisegundos. */
    private static final double UMBRAL_COMPRA_MS = 50.0;

    @Autowired
    private CompraService compraService;
    @Autowired
    private CompraDao compraDao;
    @Autowired
    private SesionDao sesionDao;
    @Autowired
    private SalaDao salaDao;
    @Autowired
    private PeliculaDao peliculaDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private EntityManager entityManager;

    private User user;
    private Sesion sesion;

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
        User nuevo = new User("rendimiento", "password", "Nombre", "Apellidos", "rendimiento@test.com");
        nuevo.setRole(User.RoleType.ESPECTADOR);
        user = userDao.save(nuevo);
        Sala sala = salaDao.save(new Sala("Sala rendimiento", 100_000));
        Pelicula pelicula = peliculaDao.save(new Pelicula("Rendimiento", "Resumen", 120));
        sesion = sesionDao.save(new Sesion(sala, pelicula, LocalDateTime.now().plusDays(1),
                new BigDecimal("7.50"), 100_000));
        MedicionJetm.reiniciar();
    }

    private void crearHistorico() {
        List<Compra> compras = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        for (int i = 0; i < COMPRAS_DEL_USUARIO; i++) {
            compras.add(new Compra(user, sesion, ahora.minusMinutes(i), 1, TARJETA, false));
        }
        compraDao.saveAll(compras);
        entityManager.flush();
        entityManager.clear();
    }

    private void consultarPagina(int pagina) throws Exception {
        compraService.findHistoricoCompras(user.getId(), pagina, TAMANO_PAGINA);
        entityManager.clear();
    }

    private void comprar() throws Exception {
        compraService.comprarEntradas(sesion.getId(), user.getId(), 1, TARJETA);
        entityManager.flush();
        entityManager.clear();
    }

    @ParameterizedTest(name = "página {0}")
    @ValueSource(ints = {0, 99})
    void historicoCumpleElUmbralConMilCompras(int pagina) throws Exception {
        crearHistorico();
        for (int i = 0; i < CALENTAMIENTO; i++) {
            consultarPagina(pagina);
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            consultarPagina(pagina);
        }
        MedicionJetm.guardarInforme("compra-integracion", "historial, página " + pagina);

        assertTrue(MedicionJetm.agregado(HISTORICO).getAverage() < UMBRAL_HISTORICO_MS);
    }

    @Test
    void comprarCumpleElUmbral() throws Exception {
        for (int i = 0; i < CALENTAMIENTO; i++) {
            comprar();
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            comprar();
        }
        MedicionJetm.guardarInforme("compra-integracion", "comprarEntradas");

        assertTrue(MedicionJetm.agregado(COMPRAR).getAverage() < UMBRAL_COMPRA_MS);
    }
}
