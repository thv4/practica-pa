package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.rest.dtos.SesionConversor;
import es.udc.paproject.backend.rest.dtos.SesionDto;
import es.udc.paproject.backend.rest.dtos.SesionResumenConversor;
import es.udc.paproject.backend.rest.dtos.SesionResumenDto;

class SesionConversorUnitTest {

    private static final Long SESION_ID = 4L;
    private static final Long PELICULA_ID = 9L;
    private static final LocalDateTime FECHA = LocalDateTime.of(2030, 2, 1, 18, 45);

    private static Sesion crearSesion(Long id, LocalDateTime fecha) {
        Pelicula pelicula = new Pelicula("Titulo", "Resumen", 135);
        pelicula.setId(PELICULA_ID);
        Sesion sesion = new Sesion(new Sala("Sala 3", 80), pelicula, fecha, new BigDecimal("6.25"), 42);
        sesion.setId(id);
        return sesion;
    }

    private static SesionDto convertir() {
        return SesionConversor.toSesionDto(crearSesion(SESION_ID, FECHA));
    }

    // ===== SesionConversor =====

    @Test
    void toSesionDtoCopiaElIdentificador() {
        assertEquals(SESION_ID, convertir().getId());
    }

    @Test
    void toSesionDtoCopiaElIdentificadorDePelicula() {
        assertEquals(PELICULA_ID, convertir().getIdPelicula());
    }

    @Test
    void toSesionDtoCopiaLaFecha() {
        assertEquals(FECHA, convertir().getFechaHora());
    }

    @Test
    void toSesionDtoCopiaElPrecio() {
        assertEquals(new BigDecimal("6.25"), convertir().getPrecio());
    }

    @Test
    void toSesionDtoCopiaLasLocalidadesLibres() {
        assertEquals(42, convertir().getLocalidadesDisponibles());
    }

    @Test
    void toSesionDtoCopiaElTituloDeLaPelicula() {
        assertEquals("Titulo", convertir().getTituloPelicula());
    }

    @Test
    void toSesionDtoCopiaLaDuracionDeLaPelicula() {
        assertEquals(135, convertir().getDuracionPelicula());
    }

    @Test
    void toSesionDtoCopiaElNombreDeLaSala() {
        assertEquals("Sala 3", convertir().getNombreSala());
    }

    @Test
    void toSesionDtoDeListaConservaElOrden() {
        List<SesionDto> dtos = SesionConversor.toSesionDto(
                List.of(crearSesion(2L, FECHA), crearSesion(1L, FECHA)));

        assertEquals(List.of(2L, 1L), dtos.stream().map(SesionDto::getId).toList());
    }

    // ===== SesionResumenConversor =====

    @Test
    void toSesionResumenDtoCopiaElIdentificador() {
        assertEquals(SESION_ID, SesionResumenConversor.toSesionResumenDto(crearSesion(SESION_ID, FECHA)).getId());
    }

    @Test
    void toSesionResumenDtoExtraeLaHora() {
        assertEquals(LocalTime.of(18, 45),
                SesionResumenConversor.toSesionResumenDto(crearSesion(SESION_ID, FECHA)).getHora());
    }

    @Test
    void toSesionResumenDtoDeListaConservaElOrden() {
        List<SesionResumenDto> dtos = SesionResumenConversor.toSesionResumenDto(
                List.of(crearSesion(1L, FECHA.withHour(16)), crearSesion(2L, FECHA.withHour(22))));

        assertEquals(List.of(LocalTime.of(16, 45), LocalTime.of(22, 45)),
                dtos.stream().map(SesionResumenDto::getHora).toList());
    }

    @Test
    void toSesionResumenDtoDeListaVaciaDevuelveListaVacia() {
        assertTrue(SesionResumenConversor.toSesionResumenDto(List.<Sesion>of()).isEmpty());
    }
}
