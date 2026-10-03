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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
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
    void deveInicializarRelacionamentosAoBuscarUsuario() {
        Usuario usuario = mock(Usuario.class);
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        assertSame(usuario, usuarioService.findById(id));
        verify(usuario).getAuthorities();
    }

    @Test
    void deveInicializarRelacionamentosAoListarUsuarios() {
        Usuario primeiro = mock(Usuario.class);
        Usuario segundo = mock(Usuario.class);
        var pageable = PageRequest.of(1, 20);
        var pagina = new PageImpl<>(List.of(primeiro, segundo), pageable, 42);
        when(usuarioRepository.findAll(pageable)).thenReturn(pagina);

        assertSame(pagina, usuarioService.findAll(pageable));
        assertEquals(42, pagina.getTotalElements());
        verify(primeiro).getAuthorities();
        verify(segundo).getAuthorities();
    }

    @Test
    void deveAtualizarProprioNomeSemAlterarCpfPerfilStatusOuSenha() {
        UUID id = UUID.randomUUID();
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        Perfil perfilOriginal = existente.getPerfil();
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.updateCurrent(id, "  Novo nome  ", null, null);

        assertEquals("Novo nome", resultado.getNome());
        assertEquals(CPF, resultado.getCpf());
        assertEquals("hash-atual", resultado.getPassword());
        assertSame(perfilOriginal, resultado.getPerfil());
        assertTrue(resultado.getAtivo());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveAlterarPropriaSenhaSomenteAposValidarSenhaAtual() {
        UUID id = UUID.randomUUID();
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(passwordEncoder.matches("senha-atual", "hash-atual")).thenReturn(true);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.updateCurrent(id, "Novo nome", SENHA, "senha-atual");

        assertEquals(SENHA_CRIPTOGRAFADA, resultado.getPassword());
        assertEquals("Novo nome", resultado.getNome());
    }

    @Test
    void deveRejeitarTrocaDeSenhaComSenhaAtualIncorretaSemModificarUsuario() {
        UUID id = UUID.randomUUID();
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(passwordEncoder.matches("incorreta", "hash-atual")).thenReturn(false);

        assertThrows(BusinessException.class,
                () -> usuarioService.updateCurrent(id, "Novo nome", SENHA, "incorreta"));

        assertEquals("Nome anterior", existente.getNome());
        assertEquals("hash-atual", existente.getPassword());
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveRejeitarTrocaDeSenhaSemSenhaAtual() {
        UUID id = UUID.randomUUID();
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente(id, "hash-atual", true)));

        assertThrows(BusinessException.class,
                () -> usuarioService.updateCurrent(id, "Novo nome", SENHA, null));
        verify(usuarioRepository, never()).save(any());
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
        assertEquals("Usuário Teste", resultado.getNome());
        assertEquals(SENHA_CRIPTOGRAFADA, resultado.getPassword());
        assertSame(perfilCarregado, resultado.getPerfil());
        verify(passwordEncoder).encode(SENHA);
        verify(usuarioRepository).save(resultado);
    }

    @Test
    void deveAtivarUsuarioPorPadraoNaCriacao() {
        Perfil perfil = perfil(UUID.randomUUID());
        Usuario usuario = usuarioNovo(perfil);
        usuario.setAtivo(null);

        when(usuarioRepository.existsByCpf(CPF)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfil.getId())).thenReturn(perfil);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.create(usuario);

        assertTrue(resultado.getAtivo());
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
        usuario.setSenha(" ");
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
        alteracoes.setSenha(null);
        alteracoes.setAtivo(null);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpfAndIdNot(CPF, id)).thenReturn(false);
        when(perfilService.findById(perfilAtualizado.getId())).thenReturn(perfilAtualizado);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.update(id, alteracoes);

        assertEquals("hash-atual", resultado.getPassword());
        assertTrue(resultado.getAtivo());
        assertSame(perfilAtualizado, resultado.getPerfil());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deveCriptografarNovaSenhaEAtualizarActive() {
        UUID id = UUID.randomUUID();
        Perfil perfilAtualizado = perfil(UUID.randomUUID());
        Usuario existente = usuarioExistente(id, "hash-atual", true);
        Usuario alteracoes = usuarioNovo(perfilAtualizado);
        alteracoes.setAtivo(false);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpfAndIdNot(CPF, id)).thenReturn(false);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_CRIPTOGRAFADA);
        when(perfilService.findById(perfilAtualizado.getId())).thenReturn(perfilAtualizado);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        Usuario resultado = usuarioService.update(id, alteracoes);

        assertEquals(SENHA_CRIPTOGRAFADA, resultado.getPassword());
        assertFalse(resultado.getAtivo());
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
                .nome("Usuário Teste")
                .senha(SENHA)
                .perfil(perfil)
                .ativo(true)
                .build();
    }

    private Usuario usuarioExistente(UUID id, String senha, boolean active) {
        return Usuario.builder()
                .id(id)
                .cpf(CPF)
                .nome("Nome anterior")
                .senha(senha)
                .perfil(perfil(UUID.randomUUID()))
                .ativo(active)
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
