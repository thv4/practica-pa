package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;

@AnalyzeClasses(
    packages = "es.udc.paproject.backend",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class LayerArchitectureTest {

    private static LayeredArchitecture capas() {
        return layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .layer("Rest").definedBy("..rest..")
        .layer("Servicios").definedBy("..model.services..")
        .layer("Entidades").definedBy("..model.entities..")
        .layer("Excepcioes").definedBy("..model.exceptions..");
    }

    @ArchTest
    static final ArchRule restNoEsAccedidaPorNingunaCapa =
        capas().whereLayer("Rest").mayNotBeAccessedByAnyLayer();

    @ArchTest
    static final ArchRule serviciosSoloAccedidosDesdeRest =
        capas().whereLayer("Servicios").mayOnlyBeAccessedByLayers("Rest");

    @ArchTest
    static final ArchRule entidadesSoloAccedidasDesdeServiciosYRest =
        capas().whereLayer("Entidades").mayOnlyBeAccessedByLayers("Servicios", "Rest");

    @ArchTest
    static final ArchRule excepcionesNoDependenDeOtrasCapas =
        noClasses().that().resideInAPackage("..model.exceptions..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..rest..", "..model.services..");
}