package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.entities.Compra;
import es.udc.paproject.backend.model.entities.Pelicula;
import es.udc.paproject.backend.model.entities.Sala;
import es.udc.paproject.backend.model.entities.Sesion;
import es.udc.paproject.backend.model.entities.User;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.MaxLocalidadesExceedException;
import es.udc.paproject.backend.model.exceptions.SesionExpiredException;
import es.udc.paproject.backend.model.services.CompraService;
import es.udc.paproject.backend.rest.controllers.CompraController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(CompraController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CompraControllerTest {
 
    private static final Long USER_ID = 1L;
    private static final Long SESION_ID = 2L;
    private static final Long COMPRA_ID = 5L;
    private static final String TARJETA = "1234567812345678";
    private static final int NUM_LOCALIDADES = 3;
    private static final int PAGE_SIZE = 2;
    private static final String BUY_URL = "/compras/buy";
    private static final String HISTORY_URL = "/compras/compras";
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private ObjectMapper mapper;
 
    @MockBean
    private CompraService compraService;
 
    private static Compra compra(Long id, int numLocalidades, LocalDateTime fechaHoraSesion) {
        Pelicula pelicula = new Pelicula("Titulo", "Resumen", 120);
        pelicula.setId(3L);
        Sala sala = new Sala("Sala 1", 100);
        Sesion sesion = new Sesion(sala, pelicula, fechaHoraSesion, new BigDecimal("7.50"), 100);
        sesion.setId(SESION_ID);
        User user = new User("user", "password", "First", "Last", "user@test.com");
        user.setId(USER_ID);
        Compra compra = new Compra(user, sesion, LocalDateTime.of(2099, 1, 1, 10, 0), numLocalidades, TARJETA, false);
        compra.setCompraId(id);
        return compra;
    }
 
    private static Compra compra() {
        return compra(COMPRA_ID, NUM_LOCALIDADES, LocalDateTime.of(2099, 6, 15, 20, 30));
    }
 
    private static Map<String, Object> buyBody(Object sesionId, Object numLocalidades, Object tarjeta) {
        Map<String, Object> body = new HashMap<>();
        body.put("sesionId", sesionId);
        body.put("numLocalidades", numLocalidades);
        body.put("tarjetaBancaria", tarjeta);
        return body;
    }
 
    private static Map<String, Object> validBuyBody() {
        return buyBody(SESION_ID, NUM_LOCALIDADES, TARJETA);
    }
 
    private ResultActions postBuy(Map<String, Object> body) throws Exception {
        return mockMvc.perform(post(BUY_URL)
            .requestAttr("userId", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)));
    }
 
    private void mockBuyOk() throws Exception {
        when(compraService.comprarEntradas(anyLong(), anyLong(), anyInt(), anyString())).thenReturn(compra());
    }
 
    private void mockHistory(List<Compra> compras, boolean hasNext) throws Exception {
        when(compraService.findHistoricoCompras(eq(USER_ID), anyInt(), eq(PAGE_SIZE)))
            .thenReturn(new SliceImpl<>(compras, PageRequest.of(0, PAGE_SIZE), hasNext));
    }
 
    static Stream<Integer> validNumLocalidadesBoundaries() {
        return Stream.of(1, 10);
    }
 
    static Stream<Integer> invalidNumLocalidadesBoundaries() {
        return Stream.of(0, -1, Integer.MIN_VALUE);
    }
 
    static Stream<String> invalidTarjetaLengths() {
        return Stream.of("", "123456781234567", "12345678123456789");
    }
 
    static Stream<String> nonNumericTarjetas() {
        return Stream.of("abcdefghijklmnop", "1234-5678-1234-5", "                ");
    }
 
    static Stream<String> randomValidTarjetas() {
        return Arbitraries.strings().numeric().ofLength(16)
            .sampleStream().limit(10);
    }
 
    @Test
    public void testPostBuy_Ok() throws Exception {
        mockBuyOk();
 
        postBuy(validBuyBody())
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostBuy_Ok_CompraId() throws Exception {
        mockBuyOk();
 
        postBuy(validBuyBody())
            .andExpect(jsonPath("$").value(COMPRA_ID));
    }
 
    @Test
    public void testPostBuy_Ok_ParametrosPasadosAlServicio() throws Exception {
        mockBuyOk();
 
        postBuy(validBuyBody());
 
        verify(compraService).comprarEntradas(SESION_ID, USER_ID, NUM_LOCALIDADES, TARJETA);
    }
 
    @ParameterizedTest
    @MethodSource("validNumLocalidadesBoundaries")
    public void testPostBuy_Ok_NumLocalidadesBoundary(int numLocalidades) throws Exception {
        mockBuyOk();
 
        postBuy(buyBody(SESION_ID, numLocalidades, TARJETA))
            .andExpect(status().isOk());
    }
 
    @ParameterizedTest
    @MethodSource("invalidNumLocalidadesBoundaries")
    public void testPostBuy_Error_NumLocalidadesBoundary(int numLocalidades) throws Exception {
        postBuy(buyBody(SESION_ID, numLocalidades, TARJETA))
            .andExpect(status().isBadRequest());
    }
 
    @ParameterizedTest
    @MethodSource("randomValidTarjetas")
    public void testPostBuy_Ok_RandomTarjeta(String tarjeta) throws Exception {
        mockBuyOk();
 
        postBuy(buyBody(SESION_ID, NUM_LOCALIDADES, tarjeta))
            .andExpect(status().isOk());
    }
 
    @ParameterizedTest
    @MethodSource("invalidTarjetaLengths")
    public void testPostBuy_Error_TarjetaLengthBoundary(String tarjeta) throws Exception {
        postBuy(buyBody(SESION_ID, NUM_LOCALIDADES, tarjeta))
            .andExpect(status().isBadRequest());
    }
 
    @ParameterizedTest
    @MethodSource("nonNumericTarjetas")
    public void testPostBuy_Error_TarjetaNoNumerica(String tarjeta) throws Exception {
        mockBuyOk();
 
        postBuy(buyBody(SESION_ID, NUM_LOCALIDADES, tarjeta))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_MissingTarjeta() throws Exception {
        Map<String, Object> body = validBuyBody();
        body.remove("tarjetaBancaria");
 
        postBuy(body)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_MissingSesionId() throws Exception {
        Map<String, Object> body = validBuyBody();
        body.remove("sesionId");
 
        postBuy(body)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_MissingNumLocalidades() throws Exception {
        Map<String, Object> body = validBuyBody();
        body.remove("numLocalidades");
 
        postBuy(body)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_InstanceNotFoundException() throws Exception {
        when(compraService.comprarEntradas(anyLong(), anyLong(), anyInt(), anyString()))
            .thenThrow(new InstanceNotFoundException("project.entities.sesion", SESION_ID));
 
        postBuy(validBuyBody())
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testPostBuy_Error_MaxLocalidadesExceedException() throws Exception {
        when(compraService.comprarEntradas(anyLong(), anyLong(), anyInt(), anyString()))
            .thenThrow(new MaxLocalidadesExceedException());
 
        postBuy(buyBody(SESION_ID, 11, TARJETA))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostBuy_Error_SesionExpiredException() throws Exception {
        when(compraService.comprarEntradas(anyLong(), anyLong(), anyInt(), anyString()))
            .thenThrow(new SesionExpiredException());
 
        postBuy(validBuyBody())
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testGetHistory_Ok() throws Exception {
        mockHistory(List.of(compra()), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(status().isOk());
    }
 
    @Test
    public void testGetHistory_Ok_Empty() throws Exception {
        mockHistory(List.of(), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.items").isEmpty());
    }
 
    @Test
    public void testGetHistory_Ok_NumeroItems() throws Exception {
        mockHistory(List.of(compra(5L, 1, LocalDateTime.of(2099, 6, 15, 20, 30)),
            compra(6L, 2, LocalDateTime.of(2099, 6, 16, 20, 30))), true);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.items.length()").value(2));
    }
 
    @Test
    public void testGetHistory_Ok_ExistMoreItems() throws Exception {
        mockHistory(List.of(compra()), true);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.existMoreItems").value(true));
    }
 
    @Test
    public void testGetHistory_Ok_NoMoreItems() throws Exception {
        mockHistory(List.of(compra()), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.existMoreItems").value(false));
    }
 
    @Test
    public void testGetHistory_Ok_PrecioTotal() throws Exception {
        mockHistory(List.of(compra()), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.items[0].precioTotal").value(22.5));
    }
 
    @Test
    public void testGetHistory_Ok_FechaHoraSesion() throws Exception {
        mockHistory(List.of(compra()), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.items[0].fechaHoraSesion").value("15-06-2099 20:30"));
    }
 
    @Test
    public void testGetHistory_Ok_TituloPelicula() throws Exception {
        mockHistory(List.of(compra()), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(jsonPath("$.items[0].tituloPelicula").value("Titulo"));
    }
 
    @Test
    public void testGetHistory_Ok_PageDefault() throws Exception {
        mockHistory(List.of(), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID));
 
        verify(compraService).findHistoricoCompras(USER_ID, 0, PAGE_SIZE);
    }
 
    @Test
    public void testGetHistory_Ok_PageParam() throws Exception {
        mockHistory(List.of(), false);
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID).param("page", "3"));
 
        verify(compraService).findHistoricoCompras(USER_ID, 3, PAGE_SIZE);
    }
 
    @Test
    public void testGetHistory_Error_InstanceNotFoundException() throws Exception {
        when(compraService.findHistoricoCompras(eq(USER_ID), anyInt(), eq(PAGE_SIZE)))
            .thenThrow(new InstanceNotFoundException("project.entities.user", USER_ID));
 
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID))
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testGetHistory_Error_NonNumericPage() throws Exception {
        mockMvc.perform(get(HISTORY_URL).requestAttr("userId", USER_ID).param("page", "abc"))
            .andExpect(status().isBadRequest());
    }
 
}

