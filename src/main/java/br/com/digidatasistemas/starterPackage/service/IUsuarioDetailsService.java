package br.com.digidatasistemas.starterPackage.service;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUsuarioDetailsService extends UserDetailsService {

    @Override
    Usuario loadUserByUsername(String cpf);
}
