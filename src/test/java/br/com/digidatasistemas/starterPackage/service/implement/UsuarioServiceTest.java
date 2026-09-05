package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.exception.ResourceNotFoundException;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.exception.ConflictException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    private static final String CPF = "00000000535";
    private static final String SENHA = "senha123";
    private static final String SENHA_CRIPTOGRAFADA = "senha-criptografada";

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IPerfilService<Perfil> perfilService;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder, perfilService);
    }

    @Test
    void deveCriarUsuarioComSenhaCriptografadaEPerfilCarregado() {
        Perfil perfilInformado = perfil(UUID.randomUUID());
        Perfil perfilCarregado = perfil(perfilInformado.getId());
        Usuario usuario = usuarioNovo(perfilInformado);

        when(usuarioRepository.existsByCpf(CPF)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfilInformado.getId())).thenReturn(perfilCarregado);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.create(usuario);

        assertEquals(CPF, resultado.getCpf());
        assertEquals("Usuário Teste", resultado.getName());
        assertEquals(SENHA_CRIPTOGRAFADA, resultado.getPassword());
        assertSame(perfilCarregado, resultado.getPerfil());
        verify(passwordEncoder).encode(SENHA);
        verify(usuarioRepository).save(resultado);
    }

    @Test
    void deveAtivarUsuarioPorPadraoNaCriacao() {
        Perfil perfil = perfil(UUID.randomUUID());
        Usuario usuario = usuarioNovo(perfil);
        usuario.setActive(null);

        when(usuarioRepository.existsByCpf(CPF)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfil.getId())).thenReturn(perfil);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.create(usuario);

        assertTrue(resultado.getActive());
    }

    @Test
    void deveRejeitarCriacaoComCpfDuplicado() {
        Usuario usuario = usuarioNovo(perfil(UUID.randomUUID()));
        when(usuarioRepository.existsByCpf(CPF)).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.create(usuario));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveRejeitarCriacaoSemSenha() {
        Usuario usuario = usuarioNovo(perfil(UUID.randomUUID()));
        usuario.setPassword(" ");
        when(usuarioRepository.existsByCpf(CPF)).thenReturn(false);

        assertThrows(BusinessException.class, () -> usuarioService.create(usuario));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void devePreservarSenhaEActiveQuandoNaoForemInformadosNaAtualizacao() {
        UUID id = UUID.randomUUID();
        Perfil perfilAtualizado = perfil(UUID.randomUUID());
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        Usuario alteracoes = usuarioNovo(perfilAtualizado);
        alteracoes.setPassword(null);
        alteracoes.setActive(null);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpfAndIdNot(CPF, id)).thenReturn(false);
        when(perfilService.findById(perfilAtualizado.getId())).thenReturn(perfilAtualizado);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.update(id, alteracoes);

        assertEquals("hash-atual", resultado.getPassword());
        assertTrue(resultado.getActive());
        assertSame(perfilAtualizado, resultado.getPerfil());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveCriptografarNovaSenhaEAtualizarActive() {
        UUID id = UUID.randomUUID();
        Perfil perfilAtualizado = perfil(UUID.randomUUID());
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        Usuario alteracoes = usuarioNovo(perfilAtualizado);
        alteracoes.setActive(false);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpfAndIdNot(CPF, id)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfilAtualizado.getId())).thenReturn(perfilAtualizado);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.update(id, alteracoes);

        assertEquals(SENHA_CRIPTOGRAFADA, resultado.getPassword());
        assertFalse(resultado.getActive());
        verify(passwordEncoder).encode(SENHA);
    }

    @Test
    void deveRejeitarAtualizacaoComCpfDeOutroUsuario() {
        UUID id = UUID.randomUUID();
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        Usuario alteracoes = usuarioNovo(perfil(UUID.randomUUID()));

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpfAndIdNot(CPF, id)).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.update(id, alteracoes));

        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void devePropagarErroQuandoPerfilNaoExiste() {
        Perfil perfil = perfil(UUID.randomUUID());
        Usuario usuario = usuarioNovo(perfil);
        ResourceNotFoundException erro = new ResourceNotFoundException("Perfil não encontrado");

        when(usuarioRepository.existsByCpf(CPF)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfil.getId())).thenThrow(erro);

        ResourceNotFoundException resultado = assertThrows(
                ResourceNotFoundException.class,
                () -> usuarioService.create(usuario)
        );

        assertSame(erro, resultado);
        verify(usuarioRepository, never()).save(any());
    }

    private Usuario usuarioNovo(Perfil perfil) {
        return Usuario.builder()
                .cpf(CPF)
                .name("Usuário Teste")
                .password(SENHA)
                .perfil(perfil)
                .active(true)
                .build();
    }

    private Usuario usuarioExistente(UUID id, String senha, boolean active) {
        return Usuario.builder()
                .id(id)
                .cpf(CPF)
                .name("Nome anterior")
                .password(senha)
                .perfil(perfil(UUID.randomUUID()))
                .active(active)
                .build();
    }

    private Perfil perfil(UUID id) {
        return Perfil.builder()
                .id(id)
                .nome("Administrador")
                .chave("ADMIN")
                .build();
    }
}
