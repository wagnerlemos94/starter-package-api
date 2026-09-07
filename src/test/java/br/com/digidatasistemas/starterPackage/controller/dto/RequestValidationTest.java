package br.com.digidatasistemas.starterPackage.controller.dto;

import br.com.digidatasistemas.starterPackage.controller.dto.request.PermissaoRequest;
import br.com.digidatasistemas.starterPackage.controller.dto.request.RecursoRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTest {

    private static jakarta.validation.ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void deveRejeitarRecursoSemCamposObrigatorios() {
        var request = new RecursoRequest();

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("nome", "ativo");
    }

    @Test
    void deveAceitarRecursoValido() {
        var request = new RecursoRequest();
        request.setNome("Usuário");
        request.setDescricao("Gerenciamento de usuários");
        request.setAtivo(true);

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void deveRejeitarPermissaoInvalida() {
        var request = new PermissaoRequest();
        request.setNome(" ");
        request.setChave("criar registro");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("nome", "chave", "ativo");
    }

    @Test
    void deveAceitarPermissaoValida() {
        var request = new PermissaoRequest();
        request.setNome("Exportar");
        request.setChave("EXPORTAR_DADOS");
        request.setDescricao("Permite exportar dados");
        request.setAtivo(true);

        assertThat(validator.validate(request)).isEmpty();
    }
}
