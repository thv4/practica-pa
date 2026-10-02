package es.udc.paproject.backend.test.integration.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class SesionControllerIntegrationTest {
 
    private static final Long NON_EXISTENT_ID = 9_000_042L;
    private static final String URL = "/sesion/{id}";
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private SesionDao sesionDao;
 
    @Autowired
    private SalaDao salaDao;
 
    @Autowired
    private PeliculaDao peliculaDao;
 
    private Sesion createSesion(String titulo, String nombreSala, LocalDateTime fechaHora) {
        Pelicula pelicula = peliculaDao.save(new Pelicula(titulo, "Resumen", 120));
        Sala sala = salaDao.save(new Sala(nombreSala, 100));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), 100));
    }
 
    private Sesion createFutureSesion() {
        return createSesion("Titulo", "Sala 1", LocalDateTime.now().plusDays(1));
    }
 
    @Test
    public void testGetDetalleSesion_Ok() throws Exception {
        Sesion sesion = createFutureSesion();
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetDetalleSesion_Ok_TituloPelicula() throws Exception {
        Sesion sesion = createSesion("Titulo persistido", "Sala 1", LocalDateTime.now().plusDays(1));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(jsonPath("$.tituloPelicula").value("Titulo persistido"));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_NombreSala() throws Exception {
        Sesion sesion = createSesion("Titulo", "Sala persistida", LocalDateTime.now().plusDays(1));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(jsonPath("$.nombreSala").value("Sala persistida"));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_FechaHora() throws Exception {
        Sesion sesion = createSesion("Titulo", "Sala 1", LocalDateTime.of(2099, 6, 15, 20, 30));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(jsonPath("$.fechaHora").value("15-06-2099 20:30"));
    }
 
    @Test
    public void testGetDetalleSesion_Ok_FechaHoraBoundary() throws Exception {
        Sesion sesion = createSesion("Titulo", "Sala 1", LocalDateTime.now().plusMinutes(1));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetDetalleSesion_Error_PastDateException() throws Exception {
        Sesion sesion = createSesion("Titulo", "Sala 1", LocalDateTime.now().minusDays(1));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetDetalleSesion_Error_PastDateException_Boundary() throws Exception {
        Sesion sesion = createSesion("Titulo", "Sala 1", LocalDateTime.now().minusMinutes(1));
 
        mockMvc.perform(get(URL, sesion.getId()))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetDetalleSesion_Error_InstanceNotFoundException() throws Exception {
        mockMvc.perform(get(URL, NON_EXISTENT_ID))
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testGetDetalleSesion_Error_NonNumericId() throws Exception {
        mockMvc.perform(get("/sesion/abc"))
            .andExpect(status().isBadRequest());
    }
 
}

