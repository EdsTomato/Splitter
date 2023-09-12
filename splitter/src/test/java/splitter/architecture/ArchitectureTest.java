package splitter.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import splitter.SplitterApplication;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = SplitterApplication.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule noDeprecatedClasses = noClasses()
            .should()
            .beAnnotatedWith(Deprecated.class);

    @ArchTest
    static final ArchRule noDeprecatedMethods = noMethods()
            .should()
            .beAnnotatedWith(Deprecated.class);

    @ArchTest
    static final ArchRule noControllerUsesRepository = noClasses()
            .that()
            .areAnnotatedWith(Controller.class)
            .should()
            .accessClassesThat()
            .areAnnotatedWith(Repository.class);

    @ArchTest
    static final ArchRule onionTest = onionArchitecture().withOptionalLayers(true)
            .domainModels("..domains..")
            .domainServices("..domains..")
            .applicationServices("..appservice..")
            .adapter("web", "..controller..")
            .adapter("rest", "..controller..")
            .adapter("db", "..persistence..");

    @ArchTest
    static final ArchRule noComponentAnnotation = noClasses()
            .should()
            .beAnnotatedWith(Component.class);

}
