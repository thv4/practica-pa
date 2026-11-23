package es.udc.paproject.backend.test.model.services.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.CompraServiceImpl;
import es.udc.paproject.backend.model.services.PermissionChecker;

@ExtendWith(MockitoExtension.class)
class CompraServiceImplUnitTest {

    private static final Long USER_ID = 1L;
    private static final Long SESION_ID = 2L;
    private static final Long COMPRA_ID = 3L;
    private static final String TARJETA = "1234567890123456";
    private static final String OTRA_TARJETA = "6543210987654321";

    @Mock
    private CompraDao compraDao;
    @Mock
    private SesionDao sesionDao;
    @Mock
    private PermissionChecker permissionChecker;

    @InjectMocks
    private CompraServiceImpl compraService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("viewer", "password", "First", "Last", "viewer@test.com");
        user.setId(USER_ID);
    }

    private Sesion crearSesion(LocalDateTime fechaHora, int localidadesLibres) {
        Sesion sesion = new Sesion(new Sala("Sala 1", 100), new Pelicula("Pelicula", "Resumen", 120),
                fechaHora, new BigDecimal("7.50"), localidadesLibres);
        sesion.setId(SESION_ID);
        return sesion;
    }

    private Sesion crearSesionFutura(int localidadesLibres) {
        return crearSesion(LocalDateTime.now().plusDays(1), localidadesLibres);
    }

    private Sesion crearSesionComenzada(int localidadesLibres) {
        return crearSesion(LocalDateTime.now().minusHours(1), localidadesLibres);
    }

    private void prepararCompra(Sesion sesion) throws InstanceNotFoundException {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesion));
    }

    private Compra prepararEntrega(Sesion sesion, boolean entregada) {
        Compra compra = new Compra(user, sesion, LocalDateTime.now(), 2, TARJETA, entregada);
        compra.setCompraId(COMPRA_ID);
        when(compraDao.findById(COMPRA_ID)).thenReturn(Optional.of(compra));
        return compra;
    }

    @Test
    void comprarEntradasValidasRestaLasLocalidadesCompradas() throws Exception {
        Sesion sesion = crearSesionFutura(10);
        prepararCompra(sesion);

        compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertEquals(7, sesion.getLocalidadesLibres());
    }

    @Test
    void comprarEntradasValidasGuardaLaCompra() throws Exception {
        prepararCompra(crearSesionFutura(10));

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        verify(compraDao).save(compra);
    }

    @Test
    void comprarEntradasValidasAsignaElUsuario() throws Exception {
        prepararCompra(crearSesionFutura(10));

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertSame(user, compra.getUser());
    }

    @Test
    void comprarEntradasValidasAsignaLaSesion() throws Exception {
        Sesion sesion = crearSesionFutura(10);
        prepararCompra(sesion);

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertSame(sesion, compra.getSesion());
    }

    @Test
    void comprarEntradasValidasRegistraElNumeroDeLocalidades() throws Exception {
        prepararCompra(crearSesionFutura(10));

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertEquals(3, compra.getNumLocalidades());
    }

    @Test
    void comprarEntradasValidasRegistraLaTarjeta() throws Exception {
        prepararCompra(crearSesionFutura(10));

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertEquals(TARJETA, compra.getTarjetaBancaria());
    }

    @Test
    void comprarEntradasValidasCreaLaCompraSinEntregar() throws Exception {
        prepararCompra(crearSesionFutura(10));

        Compra compra = compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);

        assertFalse(compra.getEntregada());
    }

    @Test
    void comprarTodasLasLocalidadesLibresDejaLaSesionSinStock() throws Exception {
        Sesion sesion = crearSesionFutura(4);
        prepararCompra(sesion);

        compraService.comprarEntradas(SESION_ID, USER_ID, 4, TARJETA);

        assertEquals(0, sesion.getLocalidadesLibres());
    }

    @Test
    void comprarEntradasConUsuarioInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID))
                .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA));
    }

    @Test
    void comprarEntradasConSesionInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA));
    }

    @ParameterizedTest(name = "{0} localidades")
    @ValueSource(ints = {-1, 0, 11})
    void comprarEntradasFueraDeRangoLanzaMaxLocalidadesExceed(int numeroEntradas) throws Exception {
        prepararCompra(crearSesionFutura(100));

        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, numeroEntradas, TARJETA));
    }

    @Test
    void comprarMasEntradasQueLasLibresLanzaMaxLocalidadesExceed() throws Exception {
        prepararCompra(crearSesionFutura(2));

        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA));
    }

    @Test
    void comprarEntradasSinStockNoGuardaLaCompra() throws Exception {
        prepararCompra(crearSesionFutura(2));

        try {
            compraService.comprarEntradas(SESION_ID, USER_ID, 3, TARJETA);
        } catch (MaxLocalidadesExceedException expected) {
            // se comprueba el efecto, no la excepción
        }

        verify(compraDao, never()).save(any());
    }

    @Test
    void comprarEntradasDeSesionComenzadaLanzaSesionExpired() throws Exception {
        prepararCompra(crearSesionComenzada(10));

        assertThrows(SesionExpiredException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA));
    }

    @Test
    void comprarEntradasDeSesionComenzadaNoModificaLasLocalidades() throws Exception {
        Sesion sesion = crearSesionComenzada(10);
        prepararCompra(sesion);

        try {
            compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA);
        } catch (SesionExpiredException expected) {
            // se comprueba el efecto, no la excepción
        }

        assertEquals(10, sesion.getLocalidadesLibres());
    }

    @Test
    void comprarEntradasDeSesionComenzadaSinStockInformaDeSesionCaducada() throws Exception {
        prepararCompra(crearSesionComenzada(0));

        assertThrows(SesionExpiredException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 1, TARJETA));
    }

    @Test
    void findHistoricoComprasPideLaPaginaYElTamanoSolicitados() throws Exception {
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
        when(compraDao.findByUser_IdOrderByFechaRegistroCompraDesc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(new SliceImpl<>(List.of()));
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);

        compraService.findHistoricoCompras(USER_ID, 2, 5);

        verify(compraDao).findByUser_IdOrderByFechaRegistroCompraDesc(eq(USER_ID), pageable.capture());
        assertEquals(PageRequest.of(2, 5), pageable.getValue());
    }

    @Test
    void findHistoricoComprasDevuelveElResultadoDelDao() throws Exception {
        Slice<Compra> esperado = new SliceImpl<>(List.of(new Compra()));
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
        when(compraDao.findByUser_IdOrderByFechaRegistroCompraDesc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(esperado);

        assertSame(esperado, compraService.findHistoricoCompras(USER_ID, 0, 10));
    }

    @Test
    void findHistoricoComprasConUsuarioInexistenteLanzaInstanceNotFound() throws Exception {
        when(permissionChecker.checkUser(USER_ID))
                .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.findHistoricoCompras(USER_ID, 0, 10));
    }

    @Test
    void findHistoricoComprasConUsuarioInexistenteNoConsultaCompras() throws Exception {
        when(permissionChecker.checkUser(USER_ID))
                .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));

        try {
            compraService.findHistoricoCompras(USER_ID, 0, 10);
        } catch (InstanceNotFoundException expected) {
            // se comprueba el efecto, no la excepción
        }

        verifyNoInteractions(compraDao);
    }

    @Test
    void entregarEntradasValidasMarcaLaCompraComoEntregada() throws Exception {
        Compra compra = prepararEntrega(crearSesionFutura(10), false);

        compraService.entregarEntradas(COMPRA_ID, TARJETA);

        assertTrue(compra.getEntregada());
    }

    @Test
    void entregarEntradasDeCompraInexistenteLanzaInstanceNotFound() {
        when(compraDao.findById(COMPRA_ID)).thenReturn(Optional.empty());

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.entregarEntradas(COMPRA_ID, TARJETA));
    }

    @Test
    void entregarEntradasConOtraTarjetaLanzaIncorrectCreditCard() {
        prepararEntrega(crearSesionFutura(10), false);

        assertThrows(IncorrectCreditCardException.class,
                () -> compraService.entregarEntradas(COMPRA_ID, OTRA_TARJETA));
    }

    @Test
    void entregarEntradasConTarjetaNulaLanzaIncorrectCreditCard() {
        prepararEntrega(crearSesionFutura(10), false);

        assertThrows(IncorrectCreditCardException.class,
                () -> compraService.entregarEntradas(COMPRA_ID, null));
    }

    @Test
    void entregarEntradasConOtraTarjetaNoMarcaLaCompra() throws Exception {
        Compra compra = prepararEntrega(crearSesionFutura(10), false);

        try {
            compraService.entregarEntradas(COMPRA_ID, OTRA_TARJETA);
        } catch (IncorrectCreditCardException expected) {
            // se comprueba el efecto, no la excepción
        }

        assertFalse(compra.getEntregada());
    }

    @Test
    void entregarEntradasYaEntregadasLanzaTicketsAlreadyDelivered() {
        prepararEntrega(crearSesionFutura(10), true);

        assertThrows(TicketsAlreadyDeliveredException.class,
                () -> compraService.entregarEntradas(COMPRA_ID, TARJETA));
    }

    @Test
    void entregarEntradasDeSesionComenzadaLanzaSessionAlreadyStarted() {
        prepararEntrega(crearSesionComenzada(10), false);

        assertThrows(SessionAlreadyStartedException.class,
                () -> compraService.entregarEntradas(COMPRA_ID, TARJETA));
    }
}
