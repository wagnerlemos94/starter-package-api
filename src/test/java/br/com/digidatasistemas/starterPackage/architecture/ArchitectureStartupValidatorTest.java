package br.com.digidatasistemas.starterPackage.architecture;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ArchitectureStartupValidatorTest {

    @Test
    void deveIgnorarTiposAuxiliaresDeclaradosDentroDosServices() {
        ArchitectureStartupValidator validator = new ArchitectureStartupValidator();

        assertDoesNotThrow(validator::afterSingletonsInstantiated);
    }
}
