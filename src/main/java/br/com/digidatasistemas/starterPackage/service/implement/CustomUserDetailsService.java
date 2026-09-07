package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import br.com.digidatasistemas.starterPackage.repository.UsuarioRepository;
import br.com.digidatasistemas.starterPackage.service.IUsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService
        implements IUsuarioDetailsService {

    private final UsuarioRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Usuario loadUserByUsername(
            String cpf) {

        Usuario usuario = repository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Inicializa perfil, recursos e permissões enquanto a sessão JPA está aberta.
        usuario.getAuthorities();

        return usuario;
    }
}
