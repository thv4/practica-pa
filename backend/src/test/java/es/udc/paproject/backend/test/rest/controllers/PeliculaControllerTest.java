package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.services.PeliculaService;
import es.udc.paproject.backend.rest.controllers.PeliculaController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(PeliculaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PeliculaControllerTest {

    private static final Long PELICULA_ID = 1L;
    private static final String TITULO = "Titulo";
    private static final String RESUMEN = "Resumen";
    private static final int DURACION = 120;
    private static final String URL = "/pelicula/peliculas/{id}";

    @Autowired 
    private MockMvc mockMvc;

    @MockBean  
    private PeliculaService peliculaService;


    private static Pelicula pelicula(Long id, int duracion) {
        Pelicula pelicula = new Pelicula(TITULO, RESUMEN, duracion);
        pelicula.setId(id);
        return pelicula;
    }
 
    static Stream<Long> idBoundaries() {
        return Stream.of(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
    }
 
    static Stream<Long> randomIds() {
        return Arbitraries.longs().between(1, Long.MAX_VALUE)
            .sampleStream().limit(10);
    }
 
    static Stream<Integer> duracionBoundaries() {
        return Stream.of(0, 1, Integer.MAX_VALUE);
    }
 
    static Stream<String> nonNumericIds() {
        return Stream.of("abc", "1.5", "9223372036854775808");
    }

    @Test
    public void testGetPelicula_Ok() throws Exception {
        
        when(peliculaService.getDetallePelicula(PELICULA_ID)).thenReturn(pelicula(PELICULA_ID, DURACION));

        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(status().isOk());
    }

        @Test
    public void testGetPelicula_Ok_Id() throws Exception {
        when(peliculaService.getDetallePelicula(PELICULA_ID)).thenReturn(pelicula(PELICULA_ID, DURACION));
 
        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(jsonPath("$.id").value(PELICULA_ID));
    }
 
    @Test
    public void testGetPelicula_Ok_Titulo() throws Exception {
        when(peliculaService.getDetallePelicula(PELICULA_ID)).thenReturn(pelicula(PELICULA_ID, DURACION));
 
        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(jsonPath("$.titulo").value(TITULO));
    }
 
    @Test
    public void testGetPelicula_Ok_Resumen() throws Exception {
        when(peliculaService.getDetallePelicula(PELICULA_ID)).thenReturn(pelicula(PELICULA_ID, DURACION));
 
        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(jsonPath("$.resumen").value(RESUMEN));
    }
 
    @ParameterizedTest
    @MethodSource("duracionBoundaries")
    public void testGetPelicula_Ok_DuracionBoundary(int duracion) throws Exception {
        when(peliculaService.getDetallePelicula(PELICULA_ID)).thenReturn(pelicula(PELICULA_ID, duracion));
 
        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(jsonPath("$.duracion").value(duracion));
    }
 
    @ParameterizedTest
    @MethodSource("randomIds")
    public void testGetPelicula_Ok_RandomId(Long id) throws Exception {
        when(peliculaService.getDetallePelicula(id)).thenReturn(pelicula(id, DURACION));
 
        mockMvc.perform(get(URL, id))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetPelicula_Error_InstanceNotFoundException() throws Exception {
        when(peliculaService.getDetallePelicula(PELICULA_ID))
            .thenThrow(new InstanceNotFoundException("project.entities.pelicula", PELICULA_ID));
 
        mockMvc.perform(get(URL, PELICULA_ID))
            .andExpect(status().isNotFound());
    }
 
    @ParameterizedTest
    @MethodSource("idBoundaries")
    public void testGetPelicula_Error_IdBoundary(Long id) throws Exception {
        when(peliculaService.getDetallePelicula(anyLong()))
            .thenThrow(new InstanceNotFoundException("project.entities.pelicula", id));
 
        mockMvc.perform(get(URL, id))
            .andExpect(status().isNotFound());
    }
 
    @ParameterizedTest
    @MethodSource("nonNumericIds")
    public void testGetPelicula_Error_NonNumericId(String id) throws Exception {
        mockMvc.perform(get("/pelicula/peliculas/" + id))
            .andExpect(status().isBadRequest());
    } 
    
}
