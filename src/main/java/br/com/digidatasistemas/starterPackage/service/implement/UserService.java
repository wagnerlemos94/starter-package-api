package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.User;
import br.com.digidatasistemas.starterPackage.repository.UserRepository;
import br.com.digidatasistemas.starterPackage.service.IUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends CrudService<User, UUID> implements IUserService<User>, UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder){
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf)
            throws UsernameNotFoundException {

        return userRepository.findByCpf(cpf)
                .orElseThrow(
                        () -> new UsernameNotFoundException(cpf)
                );
    }

    @Override
    public User create(User user) {
        String password = user.getPassword() != null ? passwordEncoder.encode(user.getPassword()) : passwordEncoder.encode(user.getCpf());
        user = User.builder()
                .cpf(user.getCpf())
                .name(user.getName())
                .password(password)
                .profile(user.getProfile())
                .build();
        return userRepository.save(user);
    }

    @Override
    public boolean hasPermission(String username, String resource, String permission) {
        return userRepository.hasPermission(username, resource, permission);
    }
}
