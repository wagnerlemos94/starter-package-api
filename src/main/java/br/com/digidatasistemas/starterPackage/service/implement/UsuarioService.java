package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.exception.ConflictException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constants.MessageConstants.*;

@Service
public class UsuarioService extends CrudService<Usuario, UUID> implements IUsuarioService<Usuario> {

    private final UsuarioRepository usuarioRepository;
    private final IPerfilService<Perfil> perfilService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, IPerfilService<Perfil> perfilService){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.perfilService = perfilService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public Page<Usuario> findAll(Pageable pageable) {
        Page<Usuario> usuarios = super.findAll(pageable);
        usuarios.forEach(this::inicializarRelacionamentos);
        return usuarios;
    }

    @Transactional
    @Override
    public Usuario findById(UUID id) {
        Usuario usuario = super.findById(id);
        return inicializarRelacionamentos(usuario);
    }

    @Transactional
    @Override
    public Usuario create(Usuario usuario) {
        validacaoCriacaoUsuario(usuario);

        String senha = passwordEncoder.encode(usuario.getPassword());
        Perfil perfil = perfilService.findById(
                usuario.getPerfil().getId()
        );

        usuario = Usuario.builder()
                .cpf(usuario.getCpf())
                .nome(usuario.getNome())
                .senha(senha)
                .perfil(perfil)
                .ativo(usuario.getAtivo() != null
                        ? usuario.getAtivo()
                        : Boolean.TRUE)
                .build();
        return usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario update(UUID id, Usuario usuario) {
        Usuario usuarioUpdate = super.findById(id);

        if (existsByCpfAndIdNot(usuario.getCpf(), id)) {
            throw new ConflictException(
                    MSG_USUARIO_JA_EXISTENTE.formatted(usuario.getCpf())
            );
        }

        usuarioUpdate.setCpf(usuario.getCpf());
        usuarioUpdate.setNome(usuario.getNome());

        if (usuario.getPassword() != null
                && !usuario.getPassword().isBlank()) {

            usuarioUpdate.setSenha(
                    passwordEncoder.encode(usuario.getPassword())
            );
        }
        Perfil perfil = perfilService.findById(
                usuario.getPerfil().getId()
        );

        usuarioUpdate.setPerfil(perfil);
        if (usuario.getAtivo() != null) {
            usuarioUpdate.setAtivo(usuario.getAtivo());
        }

        return usuarioRepository.save(usuarioUpdate);
    }

    @Override
    protected Set<String> updatableProperties() {
        return Set.of("cpf", "name", "password", "perfil", "active");
    }

    @Transactional
    @Override
    public Usuario updateCurrent(UUID id, String name, String senha, String currentPassword) {
        Usuario usuario = super.findById(id);
        if (senha != null) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, usuario.getPassword())) {
                throw new BusinessException(MSG_SENHA_ATUAL_INVALIDA);
            }
            usuario.setSenha(passwordEncoder.encode(senha));
        }
        usuario.setNome(name.trim());
        return inicializarRelacionamentos(usuarioRepository.save(usuario));
    }

    private boolean existsByCpf(String cpf){
        return usuarioRepository.existsByCpf(cpf);
    }

    private boolean existsByCpfAndIdNot(String cpf, UUID id){
        return usuarioRepository.existsByCpfAndIdNot(cpf,id);
    }

    private void validacaoCriacaoUsuario(Usuario usuario){
        if(existsByCpf(usuario.getCpf())){
            throw new ConflictException(MSG_USUARIO_JA_EXISTENTE.formatted(usuario.getCpf()));
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new BusinessException(MSG_SENHA_OBRIGATORIA);
        }
    }

    private Usuario inicializarRelacionamentos(Usuario usuario) {
        usuario.getAuthorities();
        return usuario;
    }

    @Override
    public DashboardResponse getDashboardData() {
        var dashboardResponse = usuarioRepository.buscarDadosDashboard();
        dashboardResponse.setNome("Usuários");
        return dashboardResponse;
    }
}
