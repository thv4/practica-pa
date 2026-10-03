package es.udc.paproject.backend.test.integration.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import jakarta.persistence.Entity;

@AnalyzeClasses(
    packages = "es.udc.paproject.backend",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class AnnotationRulesTest {

    @ArchTest 
    static final ArchRule controllersSonRestController = 
        classes().that().resideInAPackage("..rest.controllers..")
            .should().beAnnotatedWith(RestController.class);

    @ArchTest 
    static final ArchRule serviciosSonService = 
        classes().that().resideInAPackage("..model.services..")
            .and().haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Service.class);
    @ArchTest
    static final ArchRule serviciosSonTransacionales =
        classes().that().resideInAPackage("..model.services..")
            .and().haveSimpleNameEndingWith("ServiceImpl")
            .should().beAnnotatedWith(Transactional.class);

    @ArchTest 
    static final ArchRule entidadesSonEntity = 
        classes().that().resideInAPackage("..model.entities..")
            .and().areTopLevelClasses()
            .and().areNotInterfaces()
            .should().beAnnotatedWith(Entity.class);

    @ArchTest
    static final ArchRule daosSonRepository =
        classes().that().resideInAPackage("..model.entities..")
            .and().areInterfaces()
            .should().beAssignableTo(Repository.class);

    @ArchTest
    static final ArchRule excepcionesExtiendenException =
        classes().that().resideInAPackage("..model.exceptions..")
            .should().beAssignableTo(Exception.class);
    
}
