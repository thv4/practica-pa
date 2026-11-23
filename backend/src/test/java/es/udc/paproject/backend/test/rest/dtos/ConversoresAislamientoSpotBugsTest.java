package es.udc.paproject.backend.test.rest.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.rest.dtos.AuthenticatedUserDto;
import es.udc.paproject.backend.rest.dtos.CompraConversor;
import es.udc.paproject.backend.rest.dtos.CompraDto;
import es.udc.paproject.backend.rest.dtos.UserConversor;

/**
 * Pruebas derivadas de la revision de EI_EXPOSE_REP y EI_EXPOSE_REP2.
 * Comprueban el aislamiento de respuestas ya convertidas respecto al modelo.
 */
class ConversoresAislamientoSpotBugsTest {

    private static Compra crearCompra() {
        User usuario = new User("viewer", "password", "Nombre", "Apellidos", "viewer@test.com");
        usuario.setId(1L);
        usuario.setRole(User.RoleType.ESPECTADOR);
        Pelicula pelicula = new Pelicula("Titulo original", "Resumen", 120);
        pelicula.setId(2L);
        Sesion sesion = new Sesion(new Sala("Sala 1", 100), pelicula,
                LocalDateTime.of(2030, 1, 15, 20, 30), new BigDecimal("7.50"), 100);
        sesion.setId(3L);
        Compra compra = new Compra(usuario, sesion, LocalDateTime.of(2030, 1, 10, 12, 0),
                3, "1234567890123456", false);
        compra.setCompraId(4L);
        return compra;
    }

    @Test
    void respuestaDeCompraConservaElImporteTrasCambiarElPrecioEnLaEntidad() {
        Compra compra = crearCompra();
        CompraDto respuesta = CompraConversor.toCompraDto(compra);

        compra.getSesion().setPrecio(new BigDecimal("10.00"));

        assertEquals(0, new BigDecimal("22.50").compareTo(respuesta.getPrecioTotal()));
    }

    @Test
    void respuestaDeCompraConservaElTituloTrasCambiarLaPelicula() {
        Compra compra = crearCompra();
        CompraDto respuesta = CompraConversor.toCompraDto(compra);

        compra.getSesion().getPelicula().setTitulo("Titulo modificado");

        assertEquals("Titulo original", respuesta.getTituloPelicula());
    }

    @Test
    void listaDeRespuestasConservaLasComprasTrasVaciarLaListaDeEntidades() {
        List<Compra> compras = new ArrayList<>(List.of(crearCompra()));
        List<CompraDto> respuestas = CompraConversor.toCompraDtos(compras);

        compras.clear();

        assertEquals(List.of(4L), respuestas.stream().map(CompraDto::getCompraId).toList());
    }

    @Test
    void respuestaDeAutenticacionConservaElPerfilTrasModificarLaEntidad() {
        User usuario = crearCompra().getUser();
        AuthenticatedUserDto respuesta = UserConversor.toAuthenticatedUserDto("token-de-prueba", usuario);

        usuario.setFirstName("Nombre modificado");

        assertEquals("Nombre", respuesta.getUserDto().getFirstName());
    }
}
