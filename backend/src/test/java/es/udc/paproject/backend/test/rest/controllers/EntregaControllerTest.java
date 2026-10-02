package es.udc.paproject.backend.test.rest.controllers;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.udc.paproject.backend.model.exceptions.IncorrectCreditCardException;
import es.udc.paproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.paproject.backend.model.exceptions.SessionAlreadyStartedException;
import es.udc.paproject.backend.model.exceptions.TicketsAlreadyDeliveredException;
import es.udc.paproject.backend.model.services.CompraService;
import es.udc.paproject.backend.rest.controllers.EntregaController;
import net.jqwik.api.Arbitraries;

@WebMvcTest(EntregaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EntregaControllerTest {
 
    private static final Long USER_ID = 1L;
    private static final Long COMPRA_ID = 5L;
    private static final Long SESION_ID = 2L;
    private static final String TARJETA = "1234567812345678";
    private static final String URL = "/entregas/entregar";
 
    @Autowired
    private MockMvc mockMvc;
 
    @Autowired
    private ObjectMapper mapper;
 
    @MockBean
    private CompraService compraService;
 
    private static Map<String, Object> body(Object compraId, Object tarjeta) {
        Map<String, Object> body = new HashMap<>();
        body.put("compraId", compraId);
        body.put("tarjetaBancaria", tarjeta);
        return body;
    }
 
    private static Map<String, Object> validBody() {
        return body(COMPRA_ID, TARJETA);
    }
 
    private ResultActions postEntregar(Map<String, Object> body) throws Exception {
        return mockMvc.perform(post(URL)
            .requestAttr("userId", USER_ID)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(body)));
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
 
    static Stream<Long> compraIdBoundaries() {
        return Stream.of(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE);
    }
 
    @Test
    public void testPostEntregar_Ok() throws Exception {
        postEntregar(validBody())
            .andExpect(status().isOk());
    }
 
    @Test
    public void testPostEntregar_Ok_Success() throws Exception {
        postEntregar(validBody())
            .andExpect(jsonPath("$.success").value(true));
    }
 
    @Test
    public void testPostEntregar_Ok_Mensaje() throws Exception {
        postEntregar(validBody())
            .andExpect(jsonPath("$.mensaje").value("Entregas realizadas correctamente"));
    }
 
    @Test
    public void testPostEntregar_Ok_ParametrosPasadosAlServicio() throws Exception {
        postEntregar(validBody());
 
        verify(compraService).entregarEntradas(COMPRA_ID, TARJETA);
    }
 
    @ParameterizedTest
    @MethodSource("randomValidTarjetas")
    public void testPostEntregar_Ok_RandomTarjeta(String tarjeta) throws Exception {
        postEntregar(body(COMPRA_ID, tarjeta))
            .andExpect(status().isOk());
    }
 
    @ParameterizedTest
    @MethodSource("invalidTarjetaLengths")
    public void testPostEntregar_Error_TarjetaLengthBoundary(String tarjeta) throws Exception {
        postEntregar(body(COMPRA_ID, tarjeta))
            .andExpect(status().isBadRequest());
    }
 
    @ParameterizedTest
    @MethodSource("nonNumericTarjetas")
    public void testPostEntregar_Error_TarjetaNoNumerica(String tarjeta) throws Exception {
        postEntregar(body(COMPRA_ID, tarjeta))
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_MissingCompraId() throws Exception {
        Map<String, Object> body = validBody();
        body.remove("compraId");
 
        postEntregar(body)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_MissingTarjeta() throws Exception {
        Map<String, Object> body = validBody();
        body.remove("tarjetaBancaria");
 
        postEntregar(body)
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_InstanceNotFoundException() throws Exception {
        doThrow(new InstanceNotFoundException("project.entities.compra", COMPRA_ID))
            .when(compraService).entregarEntradas(COMPRA_ID, TARJETA);
 
        postEntregar(validBody())
            .andExpect(status().isNotFound());
    }
 
    @ParameterizedTest
    @MethodSource("compraIdBoundaries")
    public void testPostEntregar_Error_CompraIdBoundary(Long compraId) throws Exception {
        doThrow(new InstanceNotFoundException("project.entities.compra", compraId))
            .when(compraService).entregarEntradas(anyLong(), anyString());
 
        postEntregar(body(compraId, TARJETA))
            .andExpect(status().isNotFound());
    }
 
    @Test
    public void testPostEntregar_Error_IncorrectCreditCardException() throws Exception {
        doThrow(new IncorrectCreditCardException(COMPRA_ID))
            .when(compraService).entregarEntradas(COMPRA_ID, TARJETA);
 
        postEntregar(validBody())
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_TicketsAlreadyDeliveredException() throws Exception {
        doThrow(new TicketsAlreadyDeliveredException(COMPRA_ID))
            .when(compraService).entregarEntradas(COMPRA_ID, TARJETA);
 
        postEntregar(validBody())
            .andExpect(status().isBadRequest());
    }
 
    @Test
    public void testPostEntregar_Error_SessionAlreadyStartedException() throws Exception {
        doThrow(new SessionAlreadyStartedException(SESION_ID))
            .when(compraService).entregarEntradas(COMPRA_ID, TARJETA);
 
        postEntregar(validBody())
            .andExpect(status().isBadRequest());
    }
}