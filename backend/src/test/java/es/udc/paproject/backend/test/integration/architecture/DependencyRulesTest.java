package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "es.udc.paproject.backend",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class DependencyRulesTest {

    @ArchTest 
    static final ArchRule controllersNoUsanDaos = 
        noClasses().that().resideInAPackage("..rest.controllers..")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Dao");
    
    @ArchTest 
    static final ArchRule modelNoDependeDeRest = 
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("..rest..");

    @ArchTest
    static final ArchRule entidadesNoDependenDeServicios =
        noClasses().that().resideInAPackage("..model.entities..")
            .should().dependOnClassesThat().resideInAPackage("..model.services..");

    @ArchTest
    static final ArchRule modelNoDependeDeServlet =
        noClasses().that().resideInAPackage("..model..")
            .should().dependOnClassesThat().resideInAPackage("jakarta.servlet..");

    @ArchTest
    static final ArchRule nadieFueraDeRestUsaControllers =
        noClasses().that().resideOutsideOfPackage("..rest.controllers..")
            .should().dependOnClassesThat().resideInAPackage("..rest.controllers..");

    @ArchTest
    static final ArchRule dtosSoloSeUsanDesdeRest =
        classes().that().resideInAPackage("..rest.dtos..")
            .should().onlyBeAccessed().byAnyPackage("..rest..");            
}