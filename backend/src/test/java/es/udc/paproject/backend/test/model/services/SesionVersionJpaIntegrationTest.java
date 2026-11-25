package es.udc.paproject.backend.test.model.services;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import es.udc.paproject.backend.model.entities.Sesion;
import jakarta.persistence.EntityManagerFactory;

@SpringBootTest
@ActiveProfiles("test")
class SesionVersionJpaIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void sesionTieneAtributoDeVersionParaBloqueoOptimista() {
        assertTrue(entityManagerFactory.getMetamodel().entity(Sesion.class).hasVersionAttribute());
    }
}
