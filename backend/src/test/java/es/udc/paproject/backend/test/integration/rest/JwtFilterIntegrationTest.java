package es.udc.paproject.backend.test.integration.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class JwtFilterIntegrationTest {

    private static final String ENDPOINT_PROTEGIDO = "/compras/compras";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTokenFalsoEsRechazadoConUnauthorized() throws Exception {
        mockMvc.perform(get(ENDPOINT_PROTEGIDO)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-falso"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCabeceraSinPrefijoBearerSeTrataComoAnonima() throws Exception {
        mockMvc.perform(get(ENDPOINT_PROTEGIDO)
                        .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNz"))
                .andExpect(status().isForbidden());
    }
}
