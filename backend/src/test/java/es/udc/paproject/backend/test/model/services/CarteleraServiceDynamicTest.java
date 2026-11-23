package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraServiceImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class CarteleraServiceDynamicTest {

    private CarteleraServiceImpl crearServicio(SesionDao sesionDao) {
        CarteleraServiceImpl servicio = new CarteleraServiceImpl();
        ReflectionTestUtils.setField(servicio, "sesionDao", sesionDao);
        return servicio;
    }

    @Example
    void unaFechaNulaDebeInformarseComoErrorDeEntrada() {
        CarteleraServiceImpl servicio = crearServicio(mock(SesionDao.class));

        assertThrows(IllegalArgumentException.class, () -> servicio.getCartelera(null));
    }

    @Property(tries = 100)
    void fechasPasadasSeRechazan(@ForAll("diasPasados") int diasAtras) {
        CarteleraServiceImpl servicio = crearServicio(mock(SesionDao.class));
        LocalDate dia = LocalDate.now().minusDays(diasAtras);

        assertThrows(PastDateException.class, () -> servicio.getCartelera(dia));
    }

    @Property(tries = 100)
    void fechasMasAllaDeSeisDiasSeRechazan(@ForAll("diasLejanos") int diasAdelante) {
        CarteleraServiceImpl servicio = crearServicio(mock(SesionDao.class));
        LocalDate dia = LocalDate.now().plusDays(diasAdelante);

        assertThrows(InvalidPost6DaysDateException.class, () -> servicio.getCartelera(dia));
    }

    @Property(tries = 100)
    void agrupaLasSesionesDeDistintasPeliculasSinPerderlas(
            @ForAll("diasValidos") int diasAdelante,
            @ForAll("cantidadesSesiones") int cantidadPrimera,
            @ForAll("cantidadesSesiones") int cantidadSegunda) throws Exception {
        SesionDao sesionDao = mock(SesionDao.class);
        CarteleraServiceImpl servicio = crearServicio(sesionDao);
        LocalDate dia = LocalDate.now().plusDays(diasAdelante);
        Pelicula primera = new Pelicula("Pelicula A", "Resumen A", 120);
        Pelicula segunda = new Pelicula("Pelicula B", "Resumen B", 90);
        Sala sala = new Sala("Sala", 100);
        List<Sesion> sesionesPrimera = new ArrayList<>();
        List<Sesion> sesionesSegunda = new ArrayList<>();
        List<Sesion> sesiones = new ArrayList<>();
        for (int i = 0; i < cantidadPrimera; i++) {
            sesionesPrimera.add(new Sesion(sala, primera, dia.atTime(10, 0).plusMinutes(i),
                    new BigDecimal("7.50"), 100));
        }
        for (int i = 0; i < cantidadSegunda; i++) {
            sesionesSegunda.add(new Sesion(sala, segunda, dia.atTime(12, 0).plusMinutes(i),
                    new BigDecimal("7.50"), 100));
        }
        sesiones.addAll(sesionesPrimera);
        sesiones.addAll(sesionesSegunda);
        when(sesionDao.findByFechaHoraBetweenOrderByPeliculaTituloAscFechaHoraAsc(any(), any()))
                .thenReturn(sesiones);

        assertEquals(Map.of(primera, sesionesPrimera, segunda, sesionesSegunda), servicio.getCartelera(dia));
    }

    @Provide
    Arbitrary<Integer> diasPasados() {
        return Arbitraries.integers().between(2, 365);
    }

    @Provide
    Arbitrary<Integer> diasLejanos() {
        return Arbitraries.integers().between(8, 365);
    }

    @Provide
    Arbitrary<Integer> diasValidos() {
        return Arbitraries.integers().between(2, 5);
    }

    @Provide
    Arbitrary<Integer> cantidadesSesiones() {
        return Arbitraries.integers().between(1, 20);
    }
}
