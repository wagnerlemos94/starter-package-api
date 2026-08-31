package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import br.com.digidatasistemas.starterPackage.service.IUsuarioService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService extends CrudService<Usuario, UUID> implements IUsuarioService<Usuario>, UserDetailsService {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
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

    @Override
    public Usuario create(Usuario usuario) {
        String password = usuario.getPassword() != null ? passwordEncoder.encode(usuario.getPassword()) : passwordEncoder.encode(usuario.getCpf());
        usuario = Usuario.builder()
                .cpf(usuario.getCpf())
                .name(usuario.getName())
                .password(password)
                .perfil(usuario.getPerfil())
                .build();
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean hasPermission(String username, String resource, String permission) {
        return usuarioRepository.hasPermission(username, resource, permission);
    }
}
