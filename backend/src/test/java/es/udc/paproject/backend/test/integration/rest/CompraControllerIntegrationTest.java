package es.udc.paproject.backend.test.integration.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.CompraDao;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.PeliculaDao;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.SalaDao;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.SesionDao;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.entities.User.RoleType;
import es.udc.paproject.backend.model.entities.UserDao;
import es.udc.paproject.backend.rest.common.JwtGenerator;
import es.udc.paproject.backend.rest.common.JwtInfo;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CompraControllerIntegrationTest {
 
    private static final Long NON_EXISTENT_ID = 9_000_042L;
    private static final String TARJETA = "1234567812345678";
    private static final String BUY_URL = "/compras/buy";
    private static final String HISTORY_URL = "/compras/compras";
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private ObjectMapper mapper;
 
    @Autowired
    private JwtGenerator jwtGenerator;
 
    @Autowired
    private UserDao userDao;
 
    @Autowired
    private PeliculaDao peliculaDao;
 
    @Autowired
    private SalaDao salaDao;
 
    @Autowired
    private SesionDao sesionDao;
 
    @Autowired
    private CompraDao compraDao;
 
    private User createUser(String userName, RoleType role) {
        User user = new User(userName, "password", "First", "Last", userName + "@test.com");
        user.setRole(role);
        return userDao.save(user);
    }
 
    private String token(User user) {
        return "Bearer " + jwtGenerator.generate(new JwtInfo(user.getId(), user.getRole().toString()));
    }
 
    private Sesion createSesion(LocalDateTime fechaHora, int localidades) {
        Pelicula pelicula = peliculaDao.save(new Pelicula("Titulo", "Resumen", 120));
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), localidades));
    }
 
    private Sesion createFutureSesion() {
        return createSesion(LocalDateTime.now().plusDays(1), 100);
    }
 
    private Compra createCompra(User user, Sesion sesion, LocalDateTime fechaRegistro) {
        return compraDao.save(new Compra(user, sesion, fechaRegistro, 1, TARJETA, false));
    }
 
    private ResultActions postBuy(User user, Long sesionId, int numLocalidades, String tarjeta) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("sesionId", sesionId);
        body.put("numLocalidades", numLocalidades);
        body.put("tarjetaBancaria", tarjeta);
        MockHttpServletRequestBuilder request = post(BUY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body));
        if (user != null) {
            request.header("Authorization", token(user));
        }
        return mockMvc.perform(request);
    }
 
    private ResultActions getHistory(User user, String page) throws Exception {
        MockHttpServletRequestBuilder request = get(HISTORY_URL);
        if (page != null) {
            request.param("page", page);
        }
        if (user != null) {
            request.header("Authorization", token(user));
        }
        return mockMvc.perform(request);
    }
 
    @Test
    public void testPostBuy_Ok() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 2, TARJETA)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostBuy_Ok_DecrementaLocalidades() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 2, TARJETA);
 
        assertEquals(98, sesionDao.findById(sesion.getId()).get().getLocalidadesLibres());
    }
 
    @Test
    public void testPostBuy_Ok_MaxLocalidadesBoundary() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 10, TARJETA)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostBuy_Ok_TodasLasLocalidadesLibres() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createSesion(LocalDateTime.now().plusDays(1), 3);
 
        postBuy(user, sesion.getId(), 3, TARJETA)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostBuy_Error_MaxLocalidadesExceedException() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 11, TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_MaxLocalidadesExceedException_SinLocalidadesSuficientes() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createSesion(LocalDateTime.now().plusDays(1), 2);
 
        postBuy(user, sesion.getId(), 3, TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_SesionExpiredException() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createSesion(LocalDateTime.now().minusMinutes(1), 100);
 
        postBuy(user, sesion.getId(), 1, TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_InstanceNotFoundException() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
 
        postBuy(user, NON_EXISTENT_ID, 1, TARJETA)
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testPostBuy_Error_TarjetaNoNumerica() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 1, "abcdefghijklmnop")
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_SinToken() throws Exception {
        Sesion sesion = createFutureSesion();
 
        postBuy(null, sesion.getId(), 1, TARJETA)
            .andExpect(status().isForbidden());
    }
 
    @Test
    public void testPostBuy_Error_RolTaquillero() throws Exception {
        User user = createUser("taquillero", RoleType.TAQUILLERO);
        Sesion sesion = createFutureSesion();
 
        postBuy(user, sesion.getId(), 1, TARJETA)
            .andExpect(status().isForbidden());
    }
 
    @Test
    public void testGetHistory_Ok() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        createCompra(user, createFutureSesion(), LocalDateTime.now());
 
        getHistory(user, null)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetHistory_Ok_OrdenMasRecientePrimero() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
        createCompra(user, sesion, LocalDateTime.now().minusDays(2));
        Compra reciente = createCompra(user, sesion, LocalDateTime.now().minusDays(1));
 
        getHistory(user, null)
            .andExpect(jsonPath("$.items[0].compraId").value(reciente.getCompraId()));
    }
 
    @Test
    public void testGetHistory_Ok_ExistMoreItems() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
        createCompra(user, sesion, LocalDateTime.now().minusDays(3));
        createCompra(user, sesion, LocalDateTime.now().minusDays(2));
        createCompra(user, sesion, LocalDateTime.now().minusDays(1));
 
        getHistory(user, null)
            .andExpect(jsonPath("$.existMoreItems").value(true));
    }
 
    @Test
    public void testGetHistory_Ok_SegundaPagina() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createFutureSesion();
        createCompra(user, sesion, LocalDateTime.now().minusDays(3));
        createCompra(user, sesion, LocalDateTime.now().minusDays(2));
        createCompra(user, sesion, LocalDateTime.now().minusDays(1));
 
        getHistory(user, "1")
            .andExpect(jsonPath("$.items.length()").value(1));
    }
 
    @Test
    public void testGetHistory_Ok_SoloComprasDelUsuario() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
        User otro = createUser("otro", RoleType.ESPECTADOR);
        createCompra(otro, createFutureSesion(), LocalDateTime.now());
 
        getHistory(user, null)
            .andExpect(jsonPath("$.items").isEmpty());
    }
 
    @Test
    public void testGetHistory_Error_NegativePage() throws Exception {
        User user = createUser("espectador", RoleType.ESPECTADOR);
 
        getHistory(user, "-1")
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetHistory_Error_SinToken() throws Exception {
        getHistory(null, null)
            .andExpect(status().isForbidden());
    }
 
    @Test
    public void testGetHistory_Error_RolTaquillero() throws Exception {
        User user = createUser("taquillero", RoleType.TAQUILLERO);
 
        getHistory(user, null)
            .andExpect(status().isForbidden());
    }
}