package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "es.udc.paproject.backend",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class NamingConventionTest {

    @ArchTest 
    static final ArchRule contollersTerminanEnController =
        classes().that().resideInAPackage("..rest.controllers..").should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule excepcionesTerminanEnException =
        classes().that().resideInAPackage("..model.exceptions..").should().haveSimpleNameEndingWith("Exception");

    @ArchTest
    static final ArchRule daosTerminanEnDao =
        classes().that().resideInAPackage("..model.entities..")
            .and().areInterfaces().should().haveSimpleNameEndingWith("Dao");

    @ArchTest
    static final ArchRule dtosTerminanEnDtoOConversor =
        classes().that().resideInAPackage("..rest.dtos..")
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Dto")
            .orShould().haveSimpleNameEndingWith("Conversor");
    
}
