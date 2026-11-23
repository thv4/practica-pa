package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.rest.dtos.*;

/**
 * Pruebas de cobertura de accesores y constructores de DTOs sin lógica propia.
 * No prueban comportamiento de negocio: aseguran que cada setter/constructor
 * guarda el valor que después devuelve su getter.
 */
class AccesoresDtoUnitTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2030, 1, 1, 20, 0);

    // ===== CompraDto =====

    @Test
    void compraDtoSetCompraId() {
        CompraDto dto = new CompraDto();
        dto.setCompraId(1L);
        assertEquals(1L, dto.getCompraId());
    }

    @Test
    void compraDtoSetSesionId() {
        CompraDto dto = new CompraDto();
        dto.setSesionId(2L);
        assertEquals(2L, dto.getSesionId());
    }

    @Test
    void compraDtoSetTituloPelicula() {
        CompraDto dto = new CompraDto();
        dto.setTituloPelicula("Titulo");
        assertEquals("Titulo", dto.getTituloPelicula());
    }

    @Test
    void compraDtoSetFechaHoraSesion() {
        CompraDto dto = new CompraDto();
        dto.setFechaHoraSesion(FECHA);
        assertEquals(FECHA, dto.getFechaHoraSesion());
    }

    @Test
    void compraDtoSetNombreSala() {
        CompraDto dto = new CompraDto();
        dto.setNombreSala("Sala");
        assertEquals("Sala", dto.getNombreSala());
    }

    @Test
    void compraDtoSetFechaRegistroCompra() {
        CompraDto dto = new CompraDto();
        dto.setFechaRegistroCompra(FECHA);
        assertEquals(FECHA, dto.getFechaRegistroCompra());
    }

    @Test
    void compraDtoSetNumLocalidades() {
        CompraDto dto = new CompraDto();
        dto.setNumLocalidades(4);
        assertEquals(4, dto.getNumLocalidades());
    }

    @Test
    void compraDtoSetPrecioTotal() {
        CompraDto dto = new CompraDto();
        dto.setPrecioTotal(new BigDecimal("30.00"));
        assertEquals(new BigDecimal("30.00"), dto.getPrecioTotal());
    }

    @Test
    void compraDtoSetEntregada() {
        CompraDto dto = new CompraDto();
        dto.setEntregada(true);
        assertTrue(dto.isEntregada());
    }

    // ===== PeliculaDto =====

    @Test
    void peliculaDtoSetId() {
        PeliculaDto dto = new PeliculaDto();
        dto.setId(1L);
        assertEquals(1L, dto.getId());
    }

    @Test
    void peliculaDtoSetTitulo() {
        PeliculaDto dto = new PeliculaDto();
        dto.setTitulo("Titulo");
        assertEquals("Titulo", dto.getTitulo());
    }

    @Test
    void peliculaDtoSetResumen() {
        PeliculaDto dto = new PeliculaDto();
        dto.setResumen("Resumen");
        assertEquals("Resumen", dto.getResumen());
    }

    @Test
    void peliculaDtoSetDuracion() {
        PeliculaDto dto = new PeliculaDto();
        dto.setDuracion(90);
        assertEquals(90, dto.getDuracion());
    }

    // ===== PeliculaResumenDto =====

    @Test
    void peliculaResumenDtoSetId() {
        PeliculaResumenDto dto = new PeliculaResumenDto();
        dto.setId(1L);
        assertEquals(1L, dto.getId());
    }

    @Test
    void peliculaResumenDtoSetTitulo() {
        PeliculaResumenDto dto = new PeliculaResumenDto();
        dto.setTitulo("Titulo");
        assertEquals("Titulo", dto.getTitulo());
    }

    // ===== SalaDto =====

    @Test
    void salaDtoSetId() {
        SalaDto dto = new SalaDto();
        dto.setId(1L);
        assertEquals(1L, dto.getId());
    }

    @Test
    void salaDtoSetNombre() {
        SalaDto dto = new SalaDto();
        dto.setNombre("Sala");
        assertEquals("Sala", dto.getNombre());
    }

    @Test
    void salaDtoSetCapacidad() {
        SalaDto dto = new SalaDto();
        dto.setCapacidad(80);
        assertEquals(80, dto.getCapacidad());
    }

    // ===== SesionDto y SesionResumenDto =====

    @Test
    void sesionDtoConstructorCompletoConservaLasLocalidades() {
        SesionDto dto = new SesionDto(1L, 2L, "Titulo", 120, "Sala", FECHA, new BigDecimal("7.50"), 40);
        assertEquals(40, dto.getLocalidadesDisponibles());
    }

    @Test
    void sesionResumenDtoConstructorCompletoConservaLaHora() {
        SesionResumenDto dto = new SesionResumenDto(1L, LocalTime.of(18, 30));
        assertEquals(LocalTime.of(18, 30), dto.getHora());
    }

    // ===== EntregaResultDto =====

    @Test
    void entregaResultDtoSetMensaje() {
        EntregaResultDto dto = new EntregaResultDto();
        dto.setMensaje("ok");
        assertEquals("ok", dto.getMensaje());
    }

    @Test
    void entregaResultDtoSetSuccess() {
        EntregaResultDto dto = new EntregaResultDto();
        dto.setSuccess(true);
        assertTrue(dto.isSuccess());
    }

    // ===== AuthenticatedUserDto =====

    @Test
    void authenticatedUserDtoSetServiceToken() {
        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        dto.setServiceToken("token");
        assertEquals("token", dto.getServiceToken());
    }

    @Test
    void authenticatedUserDtoSetUserDto() {
        AuthenticatedUserDto dto = new AuthenticatedUserDto();
        UserDto userDto = new UserDto();
        dto.setUserDto(userDto);
        assertSame(userDto, dto.getUserDto());
    }

    // ===== BlockDto =====

    @Test
    void blockDtoSetItems() {
        BlockDto<String> dto = new BlockDto<>();
        dto.setItems(List.of("a"));
        assertEquals(List.of("a"), dto.getItems());
    }

    @Test
    void blockDtoSetExistMoreItems() {
        BlockDto<String> dto = new BlockDto<>();
        dto.setExistMoreItems(true);
        assertTrue(dto.getExistMoreItems());
    }

    // ===== CarteleraItemDto =====

    @Test
    void carteleraItemDtoSetPelicula() {
        CarteleraItemDto dto = new CarteleraItemDto(null, List.of());
        PeliculaResumenDto pelicula = new PeliculaResumenDto(1L, "Titulo");
        dto.setPelicula(pelicula);
        assertSame(pelicula, dto.getPelicula());
    }

    @Test
    void carteleraItemDtoSetSesiones() {
        CarteleraItemDto dto = new CarteleraItemDto(null, List.of());
        List<SesionResumenDto> sesiones = List.of(new SesionResumenDto(1L, LocalTime.NOON));
        dto.setSesiones(sesiones);
        assertSame(sesiones, dto.getSesiones());
    }
}
