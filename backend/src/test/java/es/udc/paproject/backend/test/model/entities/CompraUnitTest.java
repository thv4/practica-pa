package es.udc.paproject.backend.test.model.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.User;

class CompraUnitTest {

    private static Compra crearCompra(String precio, int numLocalidades) {
        Sesion sesion = new Sesion(new Sala("Sala 1", 100), new Pelicula("Titulo", "Resumen", 120),
                LocalDateTime.now().plusDays(1), new BigDecimal(precio), 100);
        return new Compra(new User(), sesion, LocalDateTime.now(), numLocalidades, "1234567890123456", false);
    }

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @CsvSource({"7.50, 1, 7.50", "7.50, 3, 22.50", "7.50, 10, 75.00", "8.33, 3, 24.99"})
    void precioTotalCalculadoMultiplicaPrecioPorLocalidades(String precio, int numLocalidades, String total) {
        BigDecimal calculado = ReflectionTestUtils.invokeMethod(crearCompra(precio, numLocalidades),
                "precioTotalCalculado");

        assertEquals(0, new BigDecimal(total).compareTo(calculado));
    }
}
