package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.rest.dtos.CompraConversor;
import es.udc.paproject.backend.rest.dtos.CompraDto;

class CompraConversorUnitTest {

    private static final Long COMPRA_ID = 7L;
    private static final Long SESION_ID = 3L;
    private static final LocalDateTime FECHA_SESION = LocalDateTime.of(2030, 1, 15, 20, 30);
    private static final LocalDateTime FECHA_COMPRA = LocalDateTime.of(2030, 1, 10, 12, 0);

    private static Compra crearCompra(String precio, int numLocalidades, boolean entregada) {
        Sesion sesion = new Sesion(new Sala("Sala 2", 100), new Pelicula("Titulo", "Resumen", 120),
                FECHA_SESION, new BigDecimal(precio), 50);
        sesion.setId(SESION_ID);
        Compra compra = new Compra(new User(), sesion, FECHA_COMPRA, numLocalidades,
                "1234567890123456", entregada);
        compra.setCompraId(COMPRA_ID);
        return compra;
    }

    private static CompraDto convertir() {
        return CompraConversor.toCompraDto(crearCompra("7.50", 3, true));
    }

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @CsvSource({"7.50, 1, 7.50", "7.50, 3, 22.50", "7.50, 10, 75.00", "8.33, 3, 24.99", "0.00, 5, 0.00"})
    void toCompraDtoCalculaElPrecioTotal(String precio, int numLocalidades, String total) {
        CompraDto dto = CompraConversor.toCompraDto(crearCompra(precio, numLocalidades, false));

        assertEquals(0, new BigDecimal(total).compareTo(dto.getPrecioTotal()));
    }

    @Test
    void toCompraDtoCopiaElIdentificadorDeCompra() {
        assertEquals(COMPRA_ID, convertir().getCompraId());
    }

    @Test
    void toCompraDtoCopiaElIdentificadorDeSesion() {
        assertEquals(SESION_ID, convertir().getSesionId());
    }

    @Test
    void toCompraDtoCopiaElTituloDeLaPelicula() {
        assertEquals("Titulo", convertir().getTituloPelicula());
    }

    @Test
    void toCompraDtoCopiaLaFechaDeLaSesion() {
        assertEquals(FECHA_SESION, convertir().getFechaHoraSesion());
    }

    @Test
    void toCompraDtoCopiaElNombreDeLaSala() {
        assertEquals("Sala 2", convertir().getNombreSala());
    }

    @Test
    void toCompraDtoCopiaLaFechaDeCompra() {
        assertEquals(FECHA_COMPRA, convertir().getFechaRegistroCompra());
    }

    @Test
    void toCompraDtoCopiaElNumeroDeLocalidades() {
        assertEquals(3, convertir().getNumLocalidades());
    }

    @Test
    void toCompraDtoCopiaElEstadoDeEntrega() {
        assertTrue(convertir().isEntregada());
    }

    @Test
    void toCompraDtosDeListaVaciaDevuelveListaVacia() {
        assertTrue(CompraConversor.toCompraDtos(List.of()).isEmpty());
    }

    @Test
    void toCompraDtosConservaElOrden() {
        Compra primera = crearCompra("7.50", 1, false);
        Compra segunda = crearCompra("7.50", 2, false);

        List<CompraDto> dtos = CompraConversor.toCompraDtos(List.of(primera, segunda));

        assertEquals(List.of(1, 2), dtos.stream().map(CompraDto::getNumLocalidades).toList());
    }
}
