package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.entities.User.RoleType;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.CompraService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompraServiceExtraTest {

    private final Long NON_EXISTENT_ID = Long.valueOf(-1);
    private final String TARJETA = "1234567890123456";

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

    private User crearUsuario(String userName) {
        User user = new User(userName, "password", "Nombre", "Apellidos", userName + "@test.com");
        user.setRole(RoleType.ESPECTADOR);
        return userDao.save(user);
    }

    private Sesion crearSesion(LocalDateTime fechaHora, int localidadesLibres) {
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        Pelicula pelicula = peliculaDao.save(new Pelicula("Matrix", "Ciencia ficción", 120));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), localidadesLibres));
    }

    private Sesion crearSesionFutura(int localidadesLibres) {
        return crearSesion(LocalDateTime.now().plusDays(1), localidadesLibres);
    }

    @Test
    public void testComprarEntradasDescuentaLocalidadesYPersiste() throws Exception {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);
        LocalDateTime antes = LocalDateTime.now();

        Compra compra = compraService.comprarEntradas(sesion.getId(), user.getId(), 3, TARJETA);

        assertEquals(17, sesion.getLocalidadesLibres());
        assertEquals(compra, compraDao.findById(compra.getCompraId()).get());
        assertFalse(compra.getFechaRegistroCompra().isBefore(antes));
        assertFalse(compra.getFechaRegistroCompra().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testComprarEntradasTodasLasDisponibles() throws Exception {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(5);

        compraService.comprarEntradas(sesion.getId(), user.getId(), 5, TARJETA);

        assertEquals(0, sesion.getLocalidadesLibres());
        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(sesion.getId(), user.getId(), 1, TARJETA));
    }

    @Test
    public void testComprarEntradasMaximoDiezPorCompra() throws Exception {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);

        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(sesion.getId(), user.getId(), 11, TARJETA));
        assertEquals(20, sesion.getLocalidadesLibres());

        compraService.comprarEntradas(sesion.getId(), user.getId(), 10, TARJETA);
        assertEquals(10, sesion.getLocalidadesLibres());
    }

    @Test
    public void testComprarEntradasNumeroNoPositivo() {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);

        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(sesion.getId(), user.getId(), 0, TARJETA));
        assertThrows(MaxLocalidadesExceedException.class,
                () -> compraService.comprarEntradas(sesion.getId(), user.getId(), -3, TARJETA));
        assertEquals(20, sesion.getLocalidadesLibres());
    }

    @Test
    public void testComprarEntradasUsuarioNoExistente() {
        Sesion sesion = crearSesionFutura(20);

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.comprarEntradas(sesion.getId(), NON_EXISTENT_ID, 2, TARJETA));
        assertEquals(20, sesion.getLocalidadesLibres());
    }

    @Test
    public void testComprarEntradasSesionNoExistente() {
        User user = crearUsuario("viewer");

        assertThrows(InstanceNotFoundException.class,
                () -> compraService.comprarEntradas(NON_EXISTENT_ID, user.getId(), 2, TARJETA));
    }

    @Test
    public void testComprarEntradasSesionYaEmpezadaNoDescuentaLocalidades() {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesion(LocalDateTime.now().minusMinutes(5), 20);

        assertThrows(SesionExpiredException.class,
                () -> compraService.comprarEntradas(sesion.getId(), user.getId(), 2, TARJETA));
        assertEquals(20, sesion.getLocalidadesLibres());
    }

    // La sesión debe tener control de concurrencia (version) para no vender
    // más localidades de las disponibles con compras simultáneas
    @Test
    public void testComprarEntradasActualizaVersionSesion() throws Exception {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);

        compraService.comprarEntradas(sesion.getId(), user.getId(), 2, TARJETA);
        entityManager.flush();

        assertNotNull(sesion.getVersion());
    }

    @Test
    public void testFindHistoricoComprasUsuarioNoExistente() {
        assertThrows(InstanceNotFoundException.class,
                () -> compraService.findHistoricoCompras(NON_EXISTENT_ID, 0, 5));
    }

    @Test
    public void testFindHistoricoComprasVacio() throws InstanceNotFoundException {
        User user = crearUsuario("viewer");

        Slice<Compra> result = compraService.findHistoricoCompras(user.getId(), 0, 5);

        assertTrue(result.getContent().isEmpty());
        assertFalse(result.hasNext());
    }

    @Test
    public void testFindHistoricoComprasSoloDelUsuario() throws InstanceNotFoundException {
        User user = crearUsuario("viewer");
        User otro = crearUsuario("otro");
        Sesion sesion = crearSesionFutura(20);
        Compra propia = compraDao.save(new Compra(user, sesion, LocalDateTime.now(), 2, TARJETA, false));
        compraDao.save(new Compra(otro, sesion, LocalDateTime.now(), 2, TARJETA, false));

        Slice<Compra> result = compraService.findHistoricoCompras(user.getId(), 0, 5);

        assertEquals(Arrays.asList(propia), result.getContent());
    }

    @Test
    public void testEntregarEntradasTarjetaNula() {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);
        Compra compra = compraDao.save(new Compra(user, sesion, LocalDateTime.now(), 2, TARJETA, false));

        assertThrows(IncorrectCreditCardException.class,
                () -> compraService.entregarEntradas(compra.getCompraId(), null));
        assertFalse(compra.getEntregada());
    }

    @Test
    public void testEntregarEntradasDosVeces() throws Exception {
        User user = crearUsuario("viewer");
        Sesion sesion = crearSesionFutura(20);
        Compra compra = compraDao.save(new Compra(user, sesion, LocalDateTime.now(), 2, TARJETA, false));

        compraService.entregarEntradas(compra.getCompraId(), TARJETA);

        assertThrows(TicketsAlreadyDeliveredException.class,
                () -> compraService.entregarEntradas(compra.getCompraId(), TARJETA));
    }
}
