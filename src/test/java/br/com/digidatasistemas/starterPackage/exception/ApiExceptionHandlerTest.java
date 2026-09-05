package br.com.digidatasistemas.starterPackage.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/user");
    }

    @Test
    void deveRetornarErrosDeValidacaoPorCampo() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "usuarioRequest");
        bindingResult.addError(new FieldError("usuarioRequest", "cpf", "CPF inválido"));
        bindingResult.addError(new FieldError("usuarioRequest", "name", "Nome é obrigatório"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Dados inválidos", response.getBody().message());
        assertEquals(2, response.getBody().errors().size());
        assertEquals(new FieldErrorResponse("cpf", "CPF inválido"), response.getBody().errors().get(0));
    }

    @Test
    void deveRetornarConflictParaRegraDeDuplicidade() {
        ResponseEntity<ErrorResponse> response = handler.handleConflict(
                new ConflictException("CPF já cadastrado"),
                request
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CPF já cadastrado", response.getBody().message());
    }

    @Test
    void deveOcultarDetalhesDaExcecaoDeBanco() {
        ResponseEntity<ErrorResponse> response = handler.handleConflict(
                new DataIntegrityViolationException("constraint usuario_cpf_key"),
                request
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertFalse(response.getBody().message().contains("usuario_cpf_key"));
    }

    @Test
    void deveRetornarMensagemSeguraEIdentificadorNoErroInterno() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("senha-do-banco"),
                request
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno no servidor", response.getBody().message());
        assertFalse(response.getBody().message().contains("senha-do-banco"));
        assertNotNull(response.getBody().errorId());
        assertFalse(response.getBody().errorId().isBlank());
        assertTrue(response.getBody().errors().isEmpty());
    }
}
