package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.CompraService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import es.udc.paproject.backend.model.entities.User.RoleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CompraServiceTest {

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

    private Sesion crearSesionPrueba(LocalDateTime fechaHora) {
        Sala sala = new Sala("Sala 1", 100);
        salaDao.save(sala);

        Pelicula pelicula = new Pelicula("Matrix", "Ciencia ficción", 120);
        peliculaDao.save(pelicula);

        Sesion sesion = new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"),20);
        sesionDao.save(sesion);

        return sesion;
    }

    private User crearUsuarioPrueba() {
        User user = new User("testviewer", "password", "Nombre", "Apellidos", "email@test.com");
        user.setRole(RoleType.USER);
        userDao.save(user);
        return user;
    }

    private Compra crearCompraPrueba(Sesion sesion, User user, String tarjeta, boolean entregada) {
        Compra compra = new Compra(user, sesion, LocalDateTime.now(), 2, tarjeta, false);
        if (entregada) {
            compra.setEntregada(true);
        }
        compraDao.save(compra);
        return compra;
    }

    @Test
    public void testEntregarEntradasExito() throws Exception {
        // Crear datos de prueba
        Sesion sesion = crearSesionPrueba(LocalDateTime.now().plusDays(1)); // Futura
        User user = crearUsuarioPrueba();
        String tarjeta = "1234567890123456";
        Compra compra = crearCompraPrueba(sesion, user, tarjeta, false);

        // Ejecutar
        compraService.entregarEntradas(compra.getCompraId(), tarjeta);

        // Verificar
        Compra compraActualizada = compraDao.findById(compra.getCompraId()).get();
        assertTrue(compraActualizada.getEntregada());
    }

    @Test
    public void testEntregarEntradasCompraNoExistente() {
        assertThrows(InstanceNotFoundException.class, () -> {
            compraService.entregarEntradas(-1L, "1234567890123456");
        });
    }

    @Test
    public void testEntregarEntradasTarjetaIncorrecta() {
        // Crear datos de prueba
        Sesion sesion = crearSesionPrueba(LocalDateTime.now().plusDays(1));
        User user = crearUsuarioPrueba();
        String tarjetaCorrecta = "1234567890123456";
        String tarjetaIncorrecta = "9999999999999999";
        Compra compra = crearCompraPrueba(sesion, user, tarjetaCorrecta, false);

        // Ejecutar y verificar
        assertThrows(IncorrectCreditCardException.class, () -> {
            compraService.entregarEntradas(compra.getCompraId(), tarjetaIncorrecta);
        });

        // Verificar que no se marcó como entregada
        Compra compraActualizada = compraDao.findById(compra.getCompraId()).get();
        assertFalse(compraActualizada.getEntregada());
    }

    @Test
    public void testEntregarEntradasYaEntregadas() {
        // Crear datos de prueba
        Sesion sesion = crearSesionPrueba(LocalDateTime.now().plusDays(1));
        User user = crearUsuarioPrueba();
        String tarjeta = "1234567890123456";
        Compra compra = crearCompraPrueba(sesion, user, tarjeta, true); // Ya entregada

        // Ejecutar y verificar
        assertThrows(TicketsAlreadyDeliveredException.class, () -> {
            compraService.entregarEntradas(compra.getCompraId(), tarjeta);
        });
    }

    @Test
    public void testEntregarEntradasSesionYaComenzada() {
        // Crear datos de prueba (sesión en el pasado)
        Sesion sesion = crearSesionPrueba(LocalDateTime.now().minusHours(1)); // Ya empezó
        User user = crearUsuarioPrueba();
        String tarjeta = "1234567890123456";
        Compra compra = crearCompraPrueba(sesion, user, tarjeta, false);

        // Ejecutar y verificar
        assertThrows(SessionAlreadyStartedException.class, () -> {
            compraService.entregarEntradas(compra.getCompraId(), tarjeta);
        });

        // Verificar que no se marcó como entregada
        Compra compraActualizada = compraDao.findById(compra.getCompraId()).get();
        assertFalse(compraActualizada.getEntregada());
    }
}