package br.com.digidatasistemas.starterPackage;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "br.com.digidatasistemas.starterPackage")
class ArchitectureTest {

    @ArchTest
    static final ArchRule SERVICES_DEVEM_IMPLEMENTAR_INTERFACE =
            classes()
                    .that()
                    .resideInAPackage("..service.implement..")
                    .should(implementarAlgumaInterface())
                    .because("services devem possuir um contrato para permitir desacoplamento e testes");

    @ArchTest
    static final ArchRule CONTROLLERS_NAO_DEVEM_DEPENDER_DE_IMPLEMENTACOES_DE_SERVICE =
            noClasses()
                    .that()
                    .resideInAPackage("..controller..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("..service.implement..")
                    .because("controllers devem receber interfaces de service, nunca implementacoes concretas");

    private static ArchCondition<JavaClass> implementarAlgumaInterface() {
        return new ArchCondition<>("implementar pelo menos uma interface") {
            @Override
            public void check(JavaClass service, ConditionEvents events) {
                if (service.getEnclosingClass().isPresent() || !service.isAnnotatedWith(Service.class)) {
                    return;
                }

                boolean implementaInterface = !service.getAllRawInterfaces().isEmpty();
                String mensagem = service.getName()
                        + (implementaInterface
                        ? " implementa uma interface"
                        : " nao implementa nenhuma interface");

                events.add(new SimpleConditionEvent(service, implementaInterface, mensagem));
            }
        };
    }
}
