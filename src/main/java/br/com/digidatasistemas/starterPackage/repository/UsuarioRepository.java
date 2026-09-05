package br.com.digidatasistemas.starterPackage.repository;

import br.com.digidatasistemas.starterPackage.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByCpf(String cpf);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM Usuario u
        JOIN u.perfil p
        JOIN p.perfilRecursos pr
        JOIN pr.recurso r
        JOIN pr.permissoes permissao
        WHERE u.name = :name
          AND r.nome = :recurso
          AND permissao.nome = :permissao
          AND u.active = true
    """)
    boolean hasPermission(
            @Param("name") String username,
            @Param("recurso") String recurso,
            @Param("permissao") String permissao
    );

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, UUID id);
}
