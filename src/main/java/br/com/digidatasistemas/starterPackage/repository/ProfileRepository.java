package br.com.digidatasistemas.starterPackage.repository;

import br.com.digidatasistemas.starterPackage.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}
