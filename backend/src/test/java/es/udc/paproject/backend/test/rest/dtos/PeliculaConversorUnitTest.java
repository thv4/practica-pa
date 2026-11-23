package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.rest.dtos.PeliculaConversor;
import es.udc.paproject.backend.rest.dtos.PeliculaDto;
import es.udc.paproject.backend.rest.dtos.PeliculaResumenConversor;
import es.udc.paproject.backend.rest.dtos.PeliculaResumenDto;
import es.udc.paproject.backend.rest.dtos.SalaConversor;
import es.udc.paproject.backend.rest.dtos.SalaDto;

class PeliculaConversorUnitTest {

    private static final Long PELICULA_ID = 8L;

    private static Pelicula crearPelicula(Long id, String titulo) {
        Pelicula pelicula = new Pelicula(titulo, "Resumen", 110);
        pelicula.setId(id);
        return pelicula;
    }

    private static PeliculaDto convertir() {
        return PeliculaConversor.toPeliculaDto(crearPelicula(PELICULA_ID, "Titulo"));
    }

    // ===== PeliculaConversor =====

    @Test
    void toPeliculaDtoCopiaElIdentificador() {
        assertEquals(PELICULA_ID, convertir().getId());
    }

    @Test
    void toPeliculaDtoCopiaElTitulo() {
        assertEquals("Titulo", convertir().getTitulo());
    }

    @Test
    void toPeliculaDtoCopiaElResumen() {
        assertEquals("Resumen", convertir().getResumen());
    }

    @Test
    void toPeliculaDtoCopiaLaDuracion() {
        assertEquals(110, convertir().getDuracion());
    }

    @Test
    void toPeliculaDtoDeListaConservaElOrden() {
        List<PeliculaDto> dtos = PeliculaConversor.toPeliculaDto(
                List.of(crearPelicula(2L, "B"), crearPelicula(1L, "A")));

        assertEquals(List.of("B", "A"), dtos.stream().map(PeliculaDto::getTitulo).toList());
    }

    // ===== PeliculaResumenConversor =====

    @Test
    void toPeliculaResumenDtoCopiaElIdentificador() {
        assertEquals(PELICULA_ID,
                PeliculaResumenConversor.toPeliculaResumenDto(crearPelicula(PELICULA_ID, "Titulo")).getId());
    }

    @Test
    void toPeliculaResumenDtoCopiaElTitulo() {
        assertEquals("Titulo",
                PeliculaResumenConversor.toPeliculaResumenDto(crearPelicula(PELICULA_ID, "Titulo")).getTitulo());
    }

    @Test
    void toPeliculaResumenDtoDeListaConservaElOrden() {
        List<PeliculaResumenDto> dtos = PeliculaResumenConversor.toPeliculaResumenDto(
                List.of(crearPelicula(2L, "B"), crearPelicula(1L, "A")));

        assertEquals(List.of("B", "A"), dtos.stream().map(PeliculaResumenDto::getTitulo).toList());
    }

    // ===== SalaConversor =====

    @Test
    void toSalaDtoCopiaElNombre() {
        assertEquals("Sala 1", SalaConversor.toSalaDto(new Sala("Sala 1", 90)).getNombre());
    }

    @Test
    void toSalaDtoCopiaLaCapacidad() {
        assertEquals(90, SalaConversor.toSalaDto(new Sala("Sala 1", 90)).getCapacidad());
    }

    @Test
    void toSalaDtoDeListaConservaElOrden() {
        List<SalaDto> dtos = SalaConversor.toSalaDto(List.of(new Sala("B", 10), new Sala("A", 20)));

        assertEquals(List.of("B", "A"), dtos.stream().map(SalaDto::getNombre).toList());
    }
}
