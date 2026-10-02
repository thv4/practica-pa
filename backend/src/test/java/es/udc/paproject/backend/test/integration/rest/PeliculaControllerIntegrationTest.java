package es.udc.paproject.backend.test.integration.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc 
@ActiveProfiles("test")
@Transactional
public class PeliculaControllerIntegrationTest {

    private static final Long NON_EXISTENT_ID = 9_000_042L;
    private static final String URL = "/pelicula/peliculas/{id}";

    @Autowired 
    private MockMvc mockMvc;

    @Autowired 
    private PeliculaDao peliculaDao;

    private Pelicula createPelicula(String titulo, int duracion) {
        return peliculaDao.save(new Pelicula(titulo, "Resumen", duracion));
    }

    static Stream<Integer> duracionBoundaries() {
        return Stream.of(0, 1, Integer.MAX_VALUE);
    }

    @Test
    public void testGetPelicula_Ok() throws Exception {

        Pelicula pelicula = createPelicula("Título", 120);

        mockMvc.perform(get(URL, pelicula.getId()))
            .andExpect(status().isOk());
    }

    @Test
    public void testGetPelicula_Ok_Titulo() throws Exception {

        Pelicula pelicula = createPelicula("Título Chulo", 120);

        mockMvc.perform(get(URL, pelicula.getId()))
            .andExpect(jsonPath("$.titulo").value(pelicula.getTitulo()));
    }

    @Test
    public void testGetPelicula_Ok_Id() throws Exception {

        Pelicula pelicula = createPelicula("Título", 120);

        mockMvc.perform(get(URL, pelicula.getId()))
            .andExpect(jsonPath("$.id").value(pelicula.getId()));
    }

    @ParameterizedTest 
    @MethodSource("duracionBoundaries")
    public void testGetPelicula_Ok_DurationBoundary(int duracion) throws Exception {

        Pelicula pelicula = createPelicula("Título", duracion);

        mockMvc.perform(get(URL, pelicula.getId()))
            .andExpect(jsonPath("$.duracion").value(duracion));
    }

    @Test
    public void testGetPelicula_Error_InstanceNotFoundException() throws Exception {
        
        mockMvc.perform(get(URL, NON_EXISTENT_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPelicula_Error_NonNumericId() throws Exception {
        
        mockMvc.perform(get("/pelicula/peliculas/abc"))
            .andExpect(status().isBadRequest());
    }
    
}
