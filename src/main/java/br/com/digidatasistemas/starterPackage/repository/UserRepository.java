package br.com.digidatasistemas.starterPackage.repository;

import br.com.digidatasistemas.starterPackage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByCpf(String cpf);

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM User u
        JOIN u.profile p
        JOIN p.profileResources pr
        JOIN pr.resource r
        JOIN pr.permissions permission
        WHERE u.name = :name
          AND r.name = :resource
          AND permission.name = :permission
          AND u.active = true
    """)
    boolean hasPermission(
            @Param("name") String username,
            @Param("resource") String resource,
            @Param("permission") String permission
    );
}
