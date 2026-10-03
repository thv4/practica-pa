package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.exceptions.InvalidPost6DaysDateException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.CarteleraService;
import es.udc.paproject.backend.rest.controllers.CarteleraController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(CarteleraController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CarteleraControllerTest {
 
    private static final String URL = "/carteleras/cartelera";
    private static final LocalDate FECHA = LocalDate.of(2099, 6, 15);
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private CarteleraService carteleraService;
 
    private static Pelicula pelicula(Long id, String titulo) {
        Pelicula pelicula = new Pelicula(titulo, "Resumen", 120);
        pelicula.setId(id);
        return pelicula;
    }
 
    private static Sesion sesion(Long id, Pelicula pelicula, LocalTime hora) {
        Sesion sesion = new Sesion(new Sala("Sala 1", 100), pelicula, FECHA.atTime(hora),
            new BigDecimal("7.50"), 100);
        sesion.setId(id);
        return sesion;
    }
 
    private static Map<Pelicula, List<Sesion>> cartelera() {
        Pelicula b = pelicula(2L, "B");
        Pelicula a = pelicula(1L, "A");
        Map<Pelicula, List<Sesion>> cartelera = new LinkedHashMap<>();
        cartelera.put(b, List.of(sesion(20L, b, LocalTime.of(18, 0)), sesion(21L, b, LocalTime.of(21, 0))));
        cartelera.put(a, List.of(sesion(10L, a, LocalTime.of(20, 0))));
        return cartelera;
    }
 
    private static Map<Pelicula, List<Sesion>> carteleraConHora(LocalTime hora) {
        Pelicula a = pelicula(1L, "A");
        Map<Pelicula, List<Sesion>> cartelera = new LinkedHashMap<>();
        cartelera.put(a, List.of(sesion(10L, a, hora)));
        return cartelera;
    }
 
    static Stream<Arguments> horaFormats() {
        return Stream.of(
            Arguments.of(LocalTime.of(0, 0), "00:00"),
            Arguments.of(LocalTime.of(23, 59), "23:59"),
            Arguments.of(LocalTime.of(9, 5, 30), "09:05"));
    }
 
    static Stream<LocalDate> randomValidFechas() {
        return Arbitraries.integers().between(0, 6)
            .map(dias -> LocalDate.now().plusDays(dias))
            .sampleStream().limit(10);
    }
 
    static Stream<String> invalidFechaFormats() {
        return Stream.of("15-06-2099", "2099/06/15", "2099-13-01", "2099-02-30", "hoy");
    }
 
    @Test
    public void testGetCartelera_Ok() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetCartelera_Ok_Empty() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(new LinkedHashMap<>());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$").isEmpty());
    }
 
    @Test
    public void testGetCartelera_Ok_NumeroPeliculas() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$.length()").value(2));
    }
 
    @Test
    public void testGetCartelera_Ok_MantieneOrdenDelServicio() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$[0].pelicula.titulo").value("B"));
    }
 
    @Test
    public void testGetCartelera_Ok_PeliculaId() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$[1].pelicula.id").value(1));
    }
 
    @Test
    public void testGetCartelera_Ok_NumeroSesiones() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$[0].sesiones.length()").value(2));
    }
 
    @Test
    public void testGetCartelera_Ok_SesionId() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$[0].sesiones[1].id").value(21));
    }
 
    @ParameterizedTest
    @MethodSource("horaFormats")
    public void testGetCartelera_Ok_HoraBoundary(LocalTime hora, String esperado) throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenReturn(carteleraConHora(hora));
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(jsonPath("$[0].sesiones[0].hora").value(esperado));
    }
 
    @Test
    public void testGetCartelera_Ok_FechaPasadaAlServicio() throws Exception {
        when(carteleraService.getCartelera(any())).thenReturn(new LinkedHashMap<>());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()));
 
        verify(carteleraService).getCartelera(FECHA);
    }
 
    @ParameterizedTest
    @MethodSource("randomValidFechas")
    public void testGetCartelera_Ok_RandomFecha(LocalDate fecha) throws Exception {
        when(carteleraService.getCartelera(fecha)).thenReturn(cartelera());
 
        mockMvc.perform(get(URL).param("fecha", fecha.toString()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetCartelera_Error_PastDateException() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenThrow(new PastDateException(FECHA.toString()));
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetCartelera_Error_InvalidPost6DaysDateException() throws Exception {
        when(carteleraService.getCartelera(FECHA)).thenThrow(new InvalidPost6DaysDateException());
 
        mockMvc.perform(get(URL).param("fecha", FECHA.toString()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetCartelera_Error_MissingFecha() throws Exception {
        mockMvc.perform(get(URL))
            .andExpect(status().isBadRequest());
    }
 
    @ParameterizedTest
    @MethodSource("invalidFechaFormats")
    public void testGetCartelera_Error_InvalidFechaFormat(String fecha) throws Exception {
        mockMvc.perform(get(URL).param("fecha", fecha))
            .andExpect(status().isBadRequest());
    }
 
}

