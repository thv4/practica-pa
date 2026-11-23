package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.MaxLocalidadesExceedException;
import es.udc.paproject.backend.model.exceptions.SesionExpiredException;
import es.udc.paproject.backend.model.services.CompraServiceImpl;
import es.udc.paproject.backend.model.services.PermissionChecker;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;

public class CompraServiceDynamicTest {

    private static final Long SESION_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final String TARJETA_VALIDA = "1234567890123456";

    @Property(tries = 100)
    void comprarConTarjetaInvalidaDebeInformarseComoErrorDeEntrada(
            @ForAll("tarjetasInvalidas") String tarjeta) throws Exception {
        Escenario escenario = crearEscenario(10, LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class,
                () -> escenario.servicio().comprarEntradas(SESION_ID, USER_ID, 1, tarjeta));
    }

    @Property(tries = 100)
    void comprarConNumeroNoPositivoLanzaMaxLocalidadesExceed(
            @ForAll("numerosNoPositivos") int numeroEntradas) throws Exception {
        Escenario escenario = crearEscenario(10, LocalDateTime.now().plusDays(1));

        assertThrows(MaxLocalidadesExceedException.class,
                () -> escenario.servicio().comprarEntradas(SESION_ID, USER_ID,
                        numeroEntradas, TARJETA_VALIDA));
    }

    @Property(tries = 100)
    void comprarMasDeDiezEntradasLanzaMaxLocalidadesExceed(
            @ForAll @IntRange(min = 11, max = 1000) int numeroEntradas) throws Exception {
        Escenario escenario = crearEscenario(1000, LocalDateTime.now().plusDays(1));

        assertThrows(MaxLocalidadesExceedException.class,
                () -> escenario.servicio().comprarEntradas(SESION_ID, USER_ID,
                        numeroEntradas, TARJETA_VALIDA));
    }

    @Property(tries = 100)
    void compraValidaDescuentaExactamenteLasLocalidadesSolicitadas(
            @ForAll @IntRange(min = 1, max = 10) int numeroEntradas,
            @ForAll @IntRange(min = 0, max = 90) int localidadesRestantes) throws Exception {
        int localidadesIniciales = numeroEntradas + localidadesRestantes;
        Escenario escenario = crearEscenario(localidadesIniciales, LocalDateTime.now().plusDays(1));

        escenario.servicio().comprarEntradas(SESION_ID, USER_ID, numeroEntradas, TARJETA_VALIDA);

        assertEquals(localidadesRestantes, escenario.sesion().getLocalidadesLibres());
    }

    @Property(tries = 100)
    void pedirUnaEntradaMasQueElStockDisponibleLanzaMaxLocalidadesExceed(
            @ForAll @IntRange(min = 0, max = 9) int localidadesLibres) throws Exception {
        Escenario escenario = crearEscenario(localidadesLibres, LocalDateTime.now().plusDays(1));

        assertThrows(MaxLocalidadesExceedException.class,
                () -> escenario.servicio().comprarEntradas(SESION_ID, USER_ID,
                        localidadesLibres + 1, TARJETA_VALIDA));
    }

    @Property(tries = 100)
    void comprarEntradasParaUnaSesionPasadaLanzaSesionExpired(
            @ForAll @IntRange(min = 1, max = 720) int horasDesdeInicio,
            @ForAll @IntRange(min = 1, max = 10) int numeroEntradas) throws Exception {
        Escenario escenario = crearEscenario(10, LocalDateTime.now().minusHours(horasDesdeInicio));

        assertThrows(SesionExpiredException.class,
                () -> escenario.servicio().comprarEntradas(SESION_ID, USER_ID,
                        numeroEntradas, TARJETA_VALIDA));
    }

    @Provide
    Arbitrary<String> tarjetasInvalidas() {
        Arbitrary<String> ejemplosYLimites = Arbitraries.of("", "123", "abcdefghijklmnop",
                "1234-5678-1234-5678", "1".repeat(15), "1".repeat(17), "123456789012345x");
        Arbitrary<String> demasiadoCortas = Arbitraries.strings().numeric().ofMinLength(0).ofMaxLength(15);
        Arbitrary<String> demasiadoLargas = Arbitraries.strings().numeric().ofMinLength(17).ofMaxLength(32);
        Arbitrary<String> longitudCorrectaPeroNoNumerica = Arbitraries.strings().numeric()
                .ofLength(15).map(digitos -> digitos + "x");
        return Arbitraries.oneOf(ejemplosYLimites, demasiadoCortas, demasiadoLargas,
                longitudCorrectaPeroNoNumerica);
    }

    @Provide
    Arbitrary<Integer> numerosNoPositivos() {
        return Arbitraries.integers().lessOrEqual(0);
    }

    private Escenario crearEscenario(int localidadesLibres, LocalDateTime fechaHora) throws Exception {
        CompraDao compraDao = mock(CompraDao.class);
        SesionDao sesionDao = mock(SesionDao.class);
        PermissionChecker permissionChecker = mock(PermissionChecker.class);
        CompraServiceImpl servicio = new CompraServiceImpl();

        ReflectionTestUtils.setField(servicio, "compraDao", compraDao);
        ReflectionTestUtils.setField(servicio, "sesionDao", sesionDao);
        ReflectionTestUtils.setField(servicio, "permissionChecker", permissionChecker);

        User user = new User("viewer", "password", "First", "Last", "viewer@test.com");
        user.setId(USER_ID);
        when(permissionChecker.checkUser(USER_ID)).thenReturn(user);

        Sala sala = new Sala("Sala 1", 1000);
        Pelicula pelicula = new Pelicula("Pelicula", "Resumen", 120);
        Sesion sesion = new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), localidadesLibres);
        when(sesionDao.findById(SESION_ID)).thenReturn(Optional.of(sesion));

        return new Escenario(servicio, sesion);
    }

    private record Escenario(CompraServiceImpl servicio, Sesion sesion) { }
}
