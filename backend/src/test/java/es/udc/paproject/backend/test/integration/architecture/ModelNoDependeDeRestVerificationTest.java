package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

import es.udc.paproject.backend.test.integration.architecture.fixtures.model.ModeloQueDependeDeRest;
import es.udc.paproject.backend.test.integration.architecture.fixtures.model.ModeloSinDependenciasDeRest;

/**
 * Comprueba que la regla "DependencyRulesTest.modelNoDependeDeRest" detecta
 * realmente una dependencia del modelo hacia la capa rest, usando clases de ejemplo.
 */
class ModelNoDependeDeRestVerificationTest {

    private static final JavaClasses MODELO_QUE_VIOLA_LA_REGLA =
            new ClassFileImporter().importClasses(ModeloQueDependeDeRest.class);

    private static final JavaClasses MODELO_QUE_CUMPLE_LA_REGLA =
            new ClassFileImporter().importClasses(ModeloSinDependenciasDeRest.class);

    private static final ArchRule REGLA_ANTERIOR =
            noClasses().that().resideInAPackage("..model..")
                    .should().dependOnClassesThat().haveSimpleNameEndingWith("..rest..");

    @Test
    void reglaCorregidaDetectaUnaDependenciaDelModeloHaciaRest() {
        assertThrows(AssertionError.class,
                () -> DependencyRulesTest.modelNoDependeDeRest.check(MODELO_QUE_VIOLA_LA_REGLA));
    }

    @Test
    void reglaCorregidaAceptaUnModeloSinDependenciasDeRest() {
        assertDoesNotThrow(
                () -> DependencyRulesTest.modelNoDependeDeRest.check(MODELO_QUE_CUMPLE_LA_REGLA));
    }

    @Test
    void reglaAnteriorNoDetectabaLaDependenciaHaciaRest() {
        assertDoesNotThrow(() -> REGLA_ANTERIOR.check(MODELO_QUE_VIOLA_LA_REGLA));
    }
}
