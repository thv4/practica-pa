package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.PastDateException;
import es.udc.paproject.backend.model.services.SesionService;
import es.udc.paproject.backend.rest.controllers.SesionController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(SesionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SesionControllerTest {
 
    private static final Long SESION_ID = 1L;
    private static final Long PELICULA_ID = 2L;
    private static final String TITULO = "Titulo";
    private static final int DURACION = 120;
    private static final String NOMBRE_SALA = "Sala 1";
    private static final LocalDateTime FECHA_HORA = LocalDateTime.of(2099, 6, 15, 20, 30);
    private static final String FECHA_HORA_FORMATEADA = "15-06-2099 20:30";
    private static final BigDecimal PRECIO = new BigDecimal("7.50");
    private static final int LOCALIDADES = 50;
    private static final String URL = "/sesion/{id}";
 
    @Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private SesionService sesionService;
 
    private static Sesion sesion(Long id, LocalDateTime fechaHora, Integer localidades) {
        Pelicula pelicula = new Pelicula(TITULO, "Resumen", DURACION);
        pelicula.setId(PELICULA_ID);
        Sala sala = new Sala(NOMBRE_SALA, 100);
        sala.setId(3L);
        Sesion sesion = new Sesion(sala, pelicula, fechaHora, PRECIO, localidades);
        sesion.setId(id);
        return sesion;
    }
 
    private static Sesion sesion() {
        return sesion(SESION_ID, FECHA_HORA, LOCALIDADES);
    }
 
    static Stream<Arguments> fechaHoraFormats() {
        return Stream.of(
            Arguments.of(LocalDateTime.of(2099, 1, 1, 0, 0), "01-01-2099 00:00"),
            Arguments.of(LocalDateTime.of(2099, 12, 31, 23, 59), "31-12-2099 23:59"),
            Arguments.of(LocalDateTime.of(2099, 2, 28, 9, 5, 59), "28-02-2099 09:05"));
    }
 
    static Stream<Integer> localidadesBoundaries() {
        return Stream.of(0, 1, Integer.MAX_VALUE);
    }
 
    static Stream<Long> idBoundaries() {
        return Stream.of(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
    }
 
    static Stream<Long> randomIds() {
        return Arbitraries.longs().between(1, Long.MAX_VALUE)
            .sampleStream().limit(10);
    }
 
    static Stream<String> nonNumericIds() {
        return Stream.of("abc", "1.5", "9223372036854775808");
    }
 
    @Test
    public void testGetDetalleSesion_Ok() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetDetalleSesion_Ok_Id() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.id").value(SESION_ID));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_IdPelicula() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.idPelicula").value(PELICULA_ID));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_TituloPelicula() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.tituloPelicula").value(TITULO));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_DuracionPelicula() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.duracionPelicula").value(DURACION));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_NombreSala() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.nombreSala").value(NOMBRE_SALA));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_Precio() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.precio").value(PRECIO.doubleValue()));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_FechaHora() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion());
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.fechaHora").value(FECHA_HORA_FORMATEADA));
    }
 
    @ParameterizedTest
    @MethodSource("fechaHoraFormats")
    public void testGetDetalleSesion_Ok_FechaHoraBoundary(LocalDateTime fechaHora, String esperado) throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion(SESION_ID, fechaHora, LOCALIDADES));
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.fechaHora").value(esperado));
    }
 
    @ParameterizedTest
    @MethodSource("localidadesBoundaries")
    public void testGetDetalleSesion_Ok_LocalidadesBoundary(Integer localidades) throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID)).thenReturn(sesion(SESION_ID, FECHA_HORA, localidades));
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(jsonPath("$.localidadesDisponibles").value(localidades));
    }
 
    @ParameterizedTest
    @MethodSource("randomIds")
    public void testGetDetalleSesion_Ok_RandomId(Long id) throws Exception {
        when(sesionService.getDetalleSesion(id)).thenReturn(sesion(id, FECHA_HORA, LOCALIDADES));
 
        mockMvc.perform(get(URL, id))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetDetalleSesion_Error_InstanceNotFoundException() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.sesion", SESION_ID));
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testGetDetalleSesion_Error_PastDateException() throws Exception {
        when(sesionService.getDetalleSesion(SESION_ID))
            .thenThrow(new PastDateException(FECHA_HORA.toString()));
 
        mockMvc.perform(get(URL, SESION_ID))
            .andExpect(status().isBadRequest());
    }
 
    @ParameterizedTest
    @MethodSource("idBoundaries")
    public void testGetDetalleSesion_Error_IdBoundary(Long id) throws Exception {
        when(sesionService.getDetalleSesion(anyLong()))
            .thenThrow(new InstanceNotFoundException("project.entities.sesion", id));
 
        mockMvc.perform(get(URL, id))
            .andExpect(status().isNotFound());
    }
 
    @ParameterizedTest
    @MethodSource("nonNumericIds")
    public void testGetDetalleSesion_Error_NonNumericId(String id) throws Exception {
        mockMvc.perform(get("/sesion/" + id))
            .andExpect(status().isBadRequest());
    }
}