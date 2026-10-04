package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.*;
import java.time.*;
import java.util.*;

import org.mockito.*;
import org.springframework.test.util.*;

import es.udc.paproject.backend.model.entities.*;
import es.udc.paproject.backend.model.services.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeProperty;

public class CompraServiceDynamicTest {

    private static final Long SESION_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final int LOCALIDADES_DISPONIBLES = 10;

    private CompraDao compraDao;
    private SesionDao sesionDao;
    private PermissionChecker permissionChecker;
    private CompraServiceImpl compraService;
    private User user;

    @BeforeProperty
    void setUp() throws Exception {
        compraDao = Mockito.mock(CompraDao.class);
        sesionDao = Mockito.mock(SesionDao.class);
        permissionChecker = Mockito.mock(PermissionChecker.class);
        compraService = new CompraServiceImpl();

        ReflectionTestUtils.setField(compraService, "compraDao", compraDao);
        ReflectionTestUtils.setField(compraService, "sesionDao", sesionDao);
        ReflectionTestUtils.setField(compraService, "permissionChecker", permissionChecker);

        user = new User("viewer", "password", "First", "Last", "viewer@test.com");
        user.setId(USER_ID);
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);
    }

    @Property
    void comprarConTarjetaInvalidaDebeInformarseComoErrorDeEntrada(
            @ForAll("tarjetasInvalidas") String tarjeta) throws Exception {
        Sesion sesion = crearSesionFutura();
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesion));

        assertThrows(IllegalArgumentException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID, 1, tarjeta));
    }

    @Property
    void comprarConNumeroDeLocalidadesInvalidoDebeInformarseComoErrorDeEntrada(
            @ForAll("numerosInvalidos") int numeroEntradas) throws Exception {
        Sesion sesion = crearSesionFutura();
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesion));

        assertThrows(IllegalArgumentException.class,
                () -> compraService.comprarEntradas(SESION_ID, USER_ID,
                        numeroEntradas, "1234567890123456"));
    }

    @Provide
    Arbitrary<String> tarjetasInvalidas() {
        return Arbitraries.of("", "123", "abcdefghijklmnop", "1234-5678-1234-5678");
    }

    @Provide
    Arbitrary<Integer> numerosInvalidos() {
        return Arbitraries.integers().between(-10, 0);
    }

    private Sesion crearSesionFutura() {
        Sala sala = new Sala("Sala 1", 100);
        Pelicula pelicula = new Pelicula("Pelicula", "Resumen", 120);
        return new Sesion(sala, pelicula, LocalDateTime.now().plusDays(1),
                new BigDecimal("7.50"), LOCALIDADES_DISPONIBLES);
    }
}
