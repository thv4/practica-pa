package es.udc.paproject.backend.test.integration.rest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class EntregaControllerIntegrationTest {
 
    private static final Long NON_EXISTENT_ID = 9_000_042L;
    private static final String TARJETA = "1234567812345678";
    private static final String OTRA_TARJETA = "8765432187654321";
    private static final String URL = "/entregas/entregar";
 
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
 
    private Sesion createSesion(LocalDateTime fechaHora) {
        Pelicula pelicula = peliculaDao.save(new Pelicula("Titulo", "Resumen", 120));
        Sala sala = salaDao.save(new Sala("Sala 1", 100));
        return sesionDao.save(new Sesion(sala, pelicula, fechaHora, new BigDecimal("7.50"), 100));
    }
 
    private Compra createCompra(LocalDateTime fechaHoraSesion, boolean entregada) {
        User comprador = createUser("espectador", RoleType.ESPECTADOR);
        Sesion sesion = createSesion(fechaHoraSesion);
        return compraDao.save(new Compra(comprador, sesion, LocalDateTime.now(), 2, TARJETA, entregada));
    }
 
    private Compra createFutureCompra() {
        return createCompra(LocalDateTime.now().plusDays(1), false);
    }
 
    private ResultActions postEntregar(User user, Long compraId, String tarjeta) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("compraId", compraId);
        body.put("tarjetaBancaria", tarjeta);
        MockHttpServletRequestBuilder request = post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body));
        if (user != null) {
            request.header("Authorization", token(user));
        }
        return mockMvc.perform(request);
    }
 
    @Test
    public void testPostEntregar_Ok() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createFutureCompra();
 
        postEntregar(taquillero, compra.getCompraId(), TARJETA)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostEntregar_Ok_MarcaEntregada() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createFutureCompra();
 
        postEntregar(taquillero, compra.getCompraId(), TARJETA);
 
        assertTrue(compraDao.findById(compra.getCompraId()).get().getEntregada());
    }
 
    @Test
    public void testPostEntregar_Ok_CompraYEntrega() throws Exception {
        User espectador = createUser("comprador", RoleType.ESPECTADOR);
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Sesion sesion = createSesion(LocalDateTime.now().plusDays(1));
        Map<String, Object> buyBody = new HashMap<>();
        buyBody.put("sesionId", sesion.getId());
        buyBody.put("numLocalidades", 2);
        buyBody.put("tarjetaBancaria", TARJETA);
        String compraId = mockMvc.perform(post("/compras/buy")
                .header("Authorization", token(espectador))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(buyBody)))
            .andReturn().getResponse().getContentAsString();
 
        postEntregar(taquillero, Long.valueOf(compraId), TARJETA)
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostEntregar_Error_TicketsAlreadyDeliveredException() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createCompra(LocalDateTime.now().plusDays(1), true);
 
        postEntregar(taquillero, compra.getCompraId(), TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_TicketsAlreadyDeliveredException_EntregaDoble() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createFutureCompra();
        postEntregar(taquillero, compra.getCompraId(), TARJETA);
 
        postEntregar(taquillero, compra.getCompraId(), TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_IncorrectCreditCardException() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createFutureCompra();
 
        postEntregar(taquillero, compra.getCompraId(), OTRA_TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_SessionAlreadyStartedException() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
        Compra compra = createCompra(LocalDateTime.now().minusMinutes(1), false);
 
        postEntregar(taquillero, compra.getCompraId(), TARJETA)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_InstanceNotFoundException() throws Exception {
        User taquillero = createUser("taquillero", RoleType.TAQUILLERO);
 
        postEntregar(taquillero, NON_EXISTENT_ID, TARJETA)
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testPostEntregar_Error_SinToken() throws Exception {
        Compra compra = createFutureCompra();
 
        postEntregar(null, compra.getCompraId(), TARJETA)
            .andExpect(status().isForbidden());
    }
 
    @Test
    public void testPostEntregar_Error_RolEspectador() throws Exception {
        User otroEspectador = createUser("otro", RoleType.ESPECTADOR);
        Compra compra = createFutureCompra();
 
        postEntregar(otroEspectador, compra.getCompraId(), TARJETA)
            .andExpect(status().isForbidden());
    }
}