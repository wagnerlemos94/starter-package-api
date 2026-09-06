package br.com.digidatasistemas.starterPackage.architecture;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

@Component
@Profile("dev")
public class ArchitectureStartupValidator implements SmartInitializingSingleton {

    private static final String BASE_PACKAGE = ArchitectureStartupValidator.class
            .getPackageName()
            .replaceFirst("\\.architecture$", "");
    private static final String SERVICE_IMPLEMENTATION_PACKAGE = BASE_PACKAGE + ".service.implement";
    private static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller";

    @Override
    public void afterSingletonsInstantiated() {
        Set<String> violations = new TreeSet<>();

        validateServices(violations);
        validateControllers(violations);

        if (!violations.isEmpty()) {
            throw new IllegalStateException(
                    "Falha na validacao arquitetural:\n - "
                            + String.join("\n - ", violations)
            );
        }
    }

    private void validateServices(Set<String> violations) {
        findClassesInPackage(SERVICE_IMPLEMENTATION_PACKAGE)
                .stream()
                .filter(serviceClass -> serviceClass.getEnclosingClass() == null)
                .filter(serviceClass -> serviceClass.isAnnotationPresent(Service.class))
                .filter(serviceClass -> serviceClass.getInterfaces().length == 0)
                .forEach(serviceClass -> violations.add(
                        serviceClass.getSimpleName()
                                + " nao implementa nenhuma interface diretamente."
                ));
    }

    private void validateControllers(Set<String> violations) {
        findClassesInPackage(CONTROLLER_PACKAGE)
                .stream()
                .forEach(controllerClass -> validateController(controllerClass, violations));
    }

    private void validateController(Class<?> controllerClass, Set<String> violations) {
        for (Constructor<?> constructor : controllerClass.getDeclaredConstructors()) {
            validateDependencyTypes(controllerClass, constructor.getParameterTypes(), violations);
        }

        for (Field field : controllerClass.getDeclaredFields()) {
            validateDependencyTypes(controllerClass, new Class<?>[]{field.getType()}, violations);
        }

        for (Method method : controllerClass.getDeclaredMethods()) {
            validateDependencyTypes(controllerClass, method.getParameterTypes(), violations);
        }
    }

    private void validateDependencyTypes(
            Class<?> controllerClass,
            Class<?>[] dependencyTypes,
            Set<String> violations) {

        for (Class<?> dependencyType : dependencyTypes) {
            if (isServiceImplementation(dependencyType)) {
                violations.add(
                        controllerClass.getSimpleName()
                                + " depende diretamente de "
                                + dependencyType.getSimpleName()
                                + ". Utilize uma interface de service."
                );
            }
        }
    }

    private boolean isServiceImplementation(Class<?> type) {
        return type.getPackageName().equals(SERVICE_IMPLEMENTATION_PACKAGE)
                || type.getPackageName().startsWith(SERVICE_IMPLEMENTATION_PACKAGE + ".");
    }

    private Set<Class<?>> findClassesInPackage(String packageName) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));

        Set<Class<?>> classes = new TreeSet<>((first, second) ->
                first.getName().compareTo(second.getName()));

        scanner.findCandidateComponents(packageName).forEach(candidate -> {
            String className = candidate.getBeanClassName();
            if (className == null) {
                return;
            }

            try {
                classes.add(ClassUtils.forName(
                        className,
                        ArchitectureStartupValidator.class.getClassLoader()
                ));
            } catch (ClassNotFoundException | LinkageError exception) {
                throw new IllegalStateException(
                        "Nao foi possivel validar a classe " + className,
                        exception
                );
            }
        });

        return classes;
    }
}
