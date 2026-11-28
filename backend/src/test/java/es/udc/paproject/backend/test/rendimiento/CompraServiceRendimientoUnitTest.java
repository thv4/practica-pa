package es.udc.paproject.backend.test.rendimiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.CompraServiceImpl;
import es.udc.paproject.backend.model.services.PermissionChecker;

/**
 * RNF-2 y RNF-3 a nivel de unidad: coste de la lógica propia de
 * findHistoricoCompras y comprarEntradas, con DAOs y PermissionChecker simulados.
 */
class CompraServiceRendimientoUnitTest {

    private static final String COMPRAR = "CompraService.comprarEntradas";
    private static final String BUSCAR_SESION = "CompraService.comprarEntradas:buscarSesion";
    private static final String GUARDAR = "CompraService.comprarEntradas:guardar";
    private static final String HISTORICO = "CompraService.findHistoricoCompras";
    private static final String HISTORICO_CONSULTA = "CompraService.findHistoricoCompras:consulta";

    private static final Long USER_ID = 1L;
    private static final Long SESION_ID = 2L;
    private static final String TARJETA = "1234567890123456";
    private static final int CALENTAMIENTO = 200;
    private static final int REPETICIONES = 1000;
    private static final int TAMANO_PAGINA = 10;
    /** Umbral RNF-2 y RNF-3 (unidad): media del servicio sin base de datos, en milisegundos. */
    private static final double UMBRAL_SERVICIO_MS = 1.0;

    private CompraDao compraDao;
    private SesionDao sesionDao;
    private PermissionChecker permissionChecker;
    private CompraServiceImpl compraService;
    private final User user = new User("viewer", "password", "Nombre", "Apellidos", "viewer@test.com");

    @BeforeAll
    static void iniciarJetm() {
        MedicionJetm.iniciar();
    }

    @AfterAll
    static void detenerJetm() {
        MedicionJetm.detener();
    }

    @BeforeEach
    void setUp() throws Exception {
        compraDao = mock(CompraDao.class);
        sesionDao = mock(SesionDao.class);
        permissionChecker = mock(PermissionChecker.class);
        compraService = new CompraServiceImpl();
        ReflectionTestUtils.setField(compraService, "compraDao", compraDao);
        ReflectionTestUtils.setField(compraService, "sesionDao", sesionDao);
        ReflectionTestUtils.setField(compraService, "permissionChecker", permissionChecker);
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
        MedicionJetm.reiniciar();
    }

    private Sesion sesionConStock(int localidades) {
        return new Sesion(new Sala("Sala", localidades), new Pelicula("Pelicula", "Resumen", 120),
                LocalDateTime.now().plusDays(1), new BigDecimal("7.50"), localidades);
    }

    private void prepararSesion() {
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesionConStock(1_000_000)));
    }

    private void prepararHistorico() {
        Sesion sesion = sesionConStock(100);
        List<Compra> pagina = new ArrayList<>();
        for (int i = 0; i < TAMANO_PAGINA; i++) {
            pagina.add(new Compra(user, sesion, LocalDateTime.now().minusMinutes(i), 1, TARJETA, false));
        }
        when(compraDao.findByUser_IdOrderByFechaRegistroCompraDesc(eq(USER_ID), any()))
                .thenReturn(new SliceImpl<>(pagina));
    }

    @Test
    void comprarSinBaseDeDatosCumpleElUmbral() throws Exception {
        prepararSesion();
        for (int i = 0; i < CALENTAMIENTO; i++) {
            compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);
        }
        MedicionJetm.guardarInforme("compra-unidad", "comprarEntradas");

        assertTrue(MedicionJetm.agregado(COMPRAR).getAverage() < UMBRAL_SERVICIO_MS);
    }

    @Test
    void historicoSinBaseDeDatosCumpleElUmbral() throws Exception {
        prepararHistorico();
        for (int i = 0; i < CALENTAMIENTO; i++) {
            compraService.findHistoricoCompras(USER_ID, 0, TAMANO_PAGINA);
        }
        MedicionJetm.reiniciar();
        for (int i = 0; i < REPETICIONES; i++) {
            compraService.findHistoricoCompras(USER_ID, 0, TAMANO_PAGINA);
        }
        MedicionJetm.guardarInforme("compra-unidad", "findHistoricoCompras");

        assertTrue(MedicionJetm.agregado(HISTORICO).getAverage() < UMBRAL_SERVICIO_MS);
    }

    @Test
    void cadaCompraRegistraUnaMedicion() throws Exception {
        prepararSesion();

        for (int i = 0; i < 5; i++) {
            compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);
        }

        assertEquals(5, MedicionJetm.agregado(COMPRAR).getMeasurements());
    }

    @Test
    void cadaConsultaDeHistoricoRegistraUnaMedicion() throws Exception {
        prepararHistorico();

        for (int i = 0; i < 5; i++) {
            compraService.findHistoricoCompras(USER_ID, 0, TAMANO_PAGINA);
        }

        assertEquals(5, MedicionJetm.agregado(HISTORICO).getMeasurements());
    }

    @Test
    void busquedaDeSesionYGuardadoSeMidenDentroDeLaCompra() throws Exception {
        prepararSesion();

        compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);

        assertEquals(Set.of(BUSCAR_SESION, GUARDAR), MedicionJetm.agregado(COMPRAR).getChilds().keySet());
    }

    @Test
    void consultaSeMideDentroDelHistorico() throws Exception {
        prepararHistorico();

        compraService.findHistoricoCompras(USER_ID, 0, TAMANO_PAGINA);

        assertEquals(Set.of(HISTORICO_CONSULTA), MedicionJetm.agregado(HISTORICO).getChilds().keySet());
    }

    @Test
    void compraRechazadaTambienSeMide() throws Exception {
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.empty());

        try {
            compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);
        } catch (InstanceNotFoundException esperada) {
            // La sesión no existe; interesa que el punto de medida se cierre igualmente.
        }

        assertEquals(1, MedicionJetm.agregado(COMPRAR).getMeasurements());
    }
}
