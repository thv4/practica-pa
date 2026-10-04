package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "es.udc.paproject.backend",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class CycleRulesTest {

    @ArchTest
    static final ArchRule modelYRestSinCiclos =
        slices().matching("es.udc.paproject.backend.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule subpaquetesDeModelSinCiclos =
        slices().matching("es.udc.paproject.backend.model.(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    static final ArchRule subpaquetesDeRestSinCiclos =
        slices().matching("es.udc.paproject.backend.rest.(*)..")
            .should().beFreeOfCycles();
}