package es.udc.paproject.backend.test.integration.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.SalaDao;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc 
@ActiveProfiles("test")
@Transactional
public class CarteleraControllerIntegrationTest {
    
    private static final String URL = "/carteleras/cartelera";

    @Autowired 
    private MockMvc mockMvc;

    @Autowired 
    private PeliculaDao peliculaDao;

    @Autowired 
    private SesionDao sesionDao;

    @Autowired 
    private SalaDao salaDao;

    private Pelicula createPelicula(String titulo) {
        return peliculaDao.save(new Pelicula(titulo, "Resumen", 120));
    }

    private Sesion createSesion(Pelicula pelicula, LocalDateTime fechaHora) {
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), 100));
    }
 
    private static LocalDate manana() {
        return LocalDate.now().plusDays(1);
    }
 
    @Test
    public void testGetCartelera_Ok() throws Exception {
        createSesion(createPelicula("A"), manana().atTime(20, 0));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetCartelera_Ok_Empty() throws Exception {
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$").isEmpty());
    }
 
    @Test
    public void testGetCartelera_Ok_OrdenPorTitulo() throws Exception {
        createSesion(createPelicula("B"), manana().atTime(18, 0));
        createSesion(createPelicula("A"), manana().atTime(20, 0));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$[0].pelicula.titulo").value("A"));
    }
 
    @Test
    public void testGetCartelera_Ok_OrdenPorHora() throws Exception {
        Pelicula pelicula = createPelicula("A");
        createSesion(pelicula, manana().atTime(21, 0));
        createSesion(pelicula, manana().atTime(18, 0));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$[0].sesiones[0].hora").value("18:00"));
    }
 
    @Test
    public void testGetCartelera_Ok_AgrupaPorPelicula() throws Exception {
        Pelicula pelicula = createPelicula("A");
        createSesion(pelicula, manana().atTime(18, 0));
        createSesion(pelicula, manana().atTime(21, 0));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$.length()").value(1));
    }
 
    @Test
    public void testGetCartelera_Ok_IncluyeSesionInicioDelDia() throws Exception {
        createSesion(createPelicula("A"), manana().atStartOfDay());
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$.length()").value(1));
    }
 
    @Test
    public void testGetCartelera_Ok_IncluyeSesionFinDelDia() throws Exception {
        createSesion(createPelicula("A"), manana().atTime(23, 59));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$.length()").value(1));
    }
 
    @Test
    public void testGetCartelera_Ok_ExcluyeSesionDiaSiguiente() throws Exception {
        createSesion(createPelicula("A"), manana().plusDays(1).atStartOfDay());
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$").isEmpty());
    }
 
    @Test
    public void testGetCartelera_Ok_HoyExcluyeSesionesYaEmpezadas() throws Exception {
        LocalDateTime haceUnMinuto = LocalDateTime.now().minusMinutes(1);
        LocalDateTime fechaHora = haceUnMinuto.toLocalDate().equals(LocalDate.now())
            ? haceUnMinuto
            : LocalDate.now().atStartOfDay();
        createSesion(createPelicula("A"), fechaHora);
 
        mockMvc.perform(get(URL).param("fecha", LocalDate.now().toString()))
            .andExpect(jsonPath("$").isEmpty());
    }
 
    @Test
    public void testGetCartelera_Ok_FechaHoy() throws Exception {
        mockMvc.perform(get(URL).param("fecha", LocalDate.now().toString()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetCartelera_Ok_FechaLimiteSeisDias() throws Exception {
        mockMvc.perform(get(URL).param("fecha", LocalDate.now().plusDays(6).toString()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetCartelera_Error_InvalidPost6DaysDateException() throws Exception {
        mockMvc.perform(get(URL).param("fecha", LocalDate.now().plusDays(7).toString()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetCartelera_Error_PastDateException() throws Exception {
        mockMvc.perform(get(URL).param("fecha", LocalDate.now().minusDays(1).toString()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetCartelera_Error_MissingFecha() throws Exception {
        mockMvc.perform(get(URL))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetCartelera_Ok_HoraFormat() throws Exception {
        createSesion(createPelicula("A"), manana().atTime(LocalTime.of(9, 5)));
 
        mockMvc.perform(get(URL).param("fecha", manana().toString()))
            .andExpect(jsonPath("$[0].sesiones[0].hora").value("09:05"));
    }

}
