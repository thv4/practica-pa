package es.udc.paproject.backend.test.model.services;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.exceptions.*;
import es.udc.paproject.backend.model.services.CompraService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import es.udc.paproject.backend.model.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.Max;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import es.udc.paproject.backend.model.entities.User.RoleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CompraServiceTest {

    @Autowired
    private UserService userService;

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

    private User signUpUser(String userName){
        User user = new User(userName, "password", "firstName", "lastName", userName + "@" + userName + ".com");

        try{
            userService.signUp(user);
        } catch (DuplicateInstanceException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private Compra createCompra(Compra c){
        Compra addCompra ;
        try{
            addCompra = compraDao.save(c);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addCompra;
    }
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
    public void testComprarEntradasValidas() throws MaxLocalidadesExceedException, InstanceNotFoundException, SesionExpiredException {

        Long userId = signUpUser("user1").getId();
        Pelicula pelicula = peliculaDao.save(new Pelicula("et", "resumen",90));
        Sala sala = salaDao.save(new Sala("primera",10));
        BigDecimal precio = BigDecimal.valueOf(15);
        Sesion sesion = sesionDao.save(new Sesion(sala,pelicula,LocalDateTime.now().plusDays(3),precio,10));
        Long sesionId = sesion.getId();
        int numEntradas = 3;
        String tarjeta = "1234567890123456";


        //Registramos compra válida
        Compra compra = compraService.comprarEntradas(sesionId,userId,numEntradas,tarjeta);

        assertNotNull(compra);
        assertEquals(userId,compra.getUser().getId());
        assertEquals(sesionId,compra.getSesion().getId());
        assertEquals(numEntradas,compra.getNumLocalidades());
        assertEquals(tarjeta,compra.getTarjetaBancaria());
        assertFalse(compra.getEntregada());
    }
    @Test
    public void testComprarEntradasSinPlazasDisponibles() throws MaxLocalidadesExceedException, InstanceNotFoundException, SesionExpiredException {
        Long userId = signUpUser("user1").getId();
        Pelicula pelicula = peliculaDao.save(new Pelicula("et", "resumen",90));
        Sala sala = salaDao.save(new Sala("primera",10));
        BigDecimal precio = BigDecimal.valueOf(15);
        Sesion sesion = sesionDao.save(new Sesion(sala,pelicula,LocalDateTime.now().plusDays(3),precio,10));
        Long sesionId = sesion.getId();
        String tarjeta = "1234567890123456";
        int numEntradas = 5;

        assertThrows(MaxLocalidadesExceedException.class,() -> compraService.comprarEntradas(sesionId,userId,17,tarjeta));


    }

    @Test
    public void testComprarEntradasSesionYaEmpezada() {

        Long userId = signUpUser("user1").getId();
        Pelicula pelicula = peliculaDao.save(new Pelicula("et", "resumen",90));
        Sala sala = salaDao.save(new Sala("primera",10));
        BigDecimal precio = BigDecimal.valueOf(15);
        int numEntradas = 3;
        String tarjeta = "1234567890123456";
        Sesion expired = sesionDao.save(new Sesion(sala,pelicula,LocalDateTime.now().minusDays(1),precio,10));
        Long expiredId = expired.getId();

        assertThrows(SesionExpiredException.class,() -> compraService.comprarEntradas(expiredId,userId,numEntradas,tarjeta));
    }

    @Test
    public void testFindHistoricoComprasValido() throws InstanceNotFoundException {

        User user = signUpUser("user");

        Sala sala = salaDao.save(new Sala("sala",20));
        Pelicula pelicula1 = peliculaDao.save(new Pelicula("tal","cual",120));
        Pelicula pelicula2 = peliculaDao.save(new Pelicula("pel","tal",100));
        BigDecimal precio = BigDecimal.valueOf(10);


        Sesion sesion = sesionDao.save(new Sesion(sala,pelicula1,LocalDateTime.now().plusDays(5),precio,20));
        Sesion sesion1 = sesionDao.save(new Sesion(sala,pelicula2,LocalDateTime.now().plusDays(5).plusMinutes(30),precio,20));
        Sesion sesion2 = sesionDao.save(new Sesion(sala,pelicula1,LocalDateTime.now().plusDays(6),precio,20));
        Sesion sesion3 = sesionDao.save(new Sesion(sala,pelicula2,LocalDateTime.now().plusDays(6).plusMinutes(40),precio,20));
        Sesion sesion4 = sesionDao.save(new Sesion(sala,pelicula1,LocalDateTime.now().plusDays(6).plusMinutes(45),precio,20));
        String tarjeta = "1234567890123456";

        Compra compra1 = createCompra(new Compra(user,sesion, LocalDateTime.now(),2,tarjeta,false));
        Compra compra2 = createCompra(new Compra(user,sesion1,LocalDateTime.now().plusMinutes(30),3,tarjeta,false)) ;
        Compra compra3 = createCompra(new Compra(user,sesion2,LocalDateTime.now().plusDays(2),2,tarjeta,false));
        Compra compra4 = createCompra(new Compra(user,sesion3,LocalDateTime.now().plusDays(3),1,tarjeta,false));
        Compra compra5 = createCompra(new Compra(user,sesion4,LocalDateTime.now().plusDays(4),2,tarjeta,false));

        Slice<Compra> result = compraService.findHistoricoCompras(user.getId(),0,5);

        assertEquals(Arrays.asList(compra5,compra4,compra3,compra2,compra1),
                result.getContent());

        Slice<Compra> pag1 = compraService.findHistoricoCompras(user.getId(), 0,2);
        assertTrue(pag1.hasNext());
        assertEquals(2,pag1.getNumberOfElements());

        Slice<Compra> pag3 = compraService.findHistoricoCompras(user.getId(), 2,2);
        assertFalse(pag3.hasNext());
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