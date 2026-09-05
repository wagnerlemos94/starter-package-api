package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.*;

@Service
public class UsuarioService extends CrudService<Usuario, UUID> implements IUsuarioService<Usuario>, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final IPerfilService<Perfil> perfilService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, IPerfilService<Perfil> perfilService){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.perfilService = perfilService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf)
            throws UsernameNotFoundException {

        return usuarioRepository.findByCpf(cpf)
                .orElseThrow(
                        () -> new UsernameNotFoundException(cpf)
                );
    }

    @Transactional
    @Override
    public Usuario create(Usuario usuario) {
        validacaoCriacaoUsuario(usuario);

        String password = passwordEncoder.encode(usuario.getPassword());
        Perfil perfil = perfilService.findById(
                usuario.getPerfil().getId()
        );

        usuario = Usuario.builder()
                .cpf(usuario.getCpf())
                .name(usuario.getName())
                .password(password)
                .perfil(perfil)
                .active(usuario.getActive() != null
                        ? usuario.getActive()
                        : Boolean.TRUE)
                .build();
        return usuarioRepository.save(usuario);
    }

    @Transactional
    @Override
    public Usuario update(UUID id, Usuario usuario) {
        Usuario usuarioUpdate = super.findById(id);

        if (existsByCpfAndIdNot(usuario.getCpf(), id)) {
            throw new BusinessException(
                    "O usuário " + usuario.getCpf() + MSG_USUARIO_JA_EXISTENTE
            );
        }

        usuarioUpdate.setCpf(usuario.getCpf());
        usuarioUpdate.setName(usuario.getName());

        if (usuario.getPassword() != null
                && !usuario.getPassword().isBlank()) {

            usuarioUpdate.setPassword(
                    passwordEncoder.encode(usuario.getPassword())
            );
        }
        Perfil perfil = perfilService.findById(
                usuario.getPerfil().getId()
        );

        usuarioUpdate.setPerfil(perfil);
        if (usuario.getActive() != null) {
            usuarioUpdate.setActive(usuario.getActive());
        }

        return usuarioRepository.save(usuarioUpdate);
    }


    @Override
    public boolean hasPermission(String username, String resource, String permission) {
        return usuarioRepository.hasPermission(username, resource, permission);
    }

    private boolean existsByCpf(String cpf){
        return usuarioRepository.existsByCpf(cpf);
    }

    private boolean existsByCpfAndIdNot(String cpf, UUID id){
        return usuarioRepository.existsByCpfAndIdNot(cpf,id);
    }

    private void validacaoCriacaoUsuario(Usuario usuario){
        if(existsByCpf(usuario.getCpf())){
            throw new BusinessException("O usuário " + usuario.getCpf() + MSG_USUARIO_JA_EXISTENTE);
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new BusinessException("Senha é obrigatória.");
        }
    }
}
