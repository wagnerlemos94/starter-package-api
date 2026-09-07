package br.com.digidatasistemas.starterPackage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Perfil perfil;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new LinkedHashSet<>();

        if (perfil == null || !Boolean.TRUE.equals(perfil.getAtivo())) {
            return authorities;
        }

        // ROLE_GESTOR, ROLE_ADMINISTRADOR, etc.
        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + perfil.getChave()
                )
        );

        // Permissões dos recursos
        List<PerfilRecurso> perfilRecursos = perfil.getPerfilRecursos() == null
                ? List.of()
                : perfil.getPerfilRecursos();

        perfilRecursos.stream()
                .filter(Objects::nonNull)
                .filter(profileResource -> profileResource.getRecurso() != null)
                .filter(profileResource -> Boolean.TRUE.equals(profileResource.getRecurso().getAtivo()))
                .forEach(profileResource -> {

            String resource = profileResource
                    .getRecurso()
                    .getChave();

            List<Permissao> permissoes = profileResource.getPermissoes() == null
                    ? List.of()
                    : profileResource.getPermissoes();

            permissoes.stream()
                    .filter(Objects::nonNull)
                    .filter(permission -> Boolean.TRUE.equals(permission.getAtivo()))
                    .forEach(permission -> {

                authorities.add(
                        new SimpleGrantedAuthority(
                                resource + ":" + permission.getChave()
                        )
                );
                    });
        });

        return authorities;
    }

    @Override
    public String getUsername() {
        return cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active)
                && perfil != null
                && Boolean.TRUE.equals(perfil.getAtivo());
    }
}
