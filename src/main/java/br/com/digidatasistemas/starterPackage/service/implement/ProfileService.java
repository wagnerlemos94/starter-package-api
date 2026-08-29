package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Profile;
import br.com.digidatasistemas.starterPackage.model.ProfileResource;
import br.com.digidatasistemas.starterPackage.repository.ProfileRepository;
import br.com.digidatasistemas.starterPackage.service.IProfileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
public class ProfileService extends CrudService<Profile, UUID> implements IProfileService<Profile> {

    private ProfileRepository repository;

    public ProfileService(ProfileRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Profile create(Profile profile) {
        profile.setKey(profile.getName().toUpperCase());
        return repository.save(profile);
    }

    public Profile update(UUID id, Profile profile) {

        Profile profileSave = this.findById(id);

        profileSave.setName(profile.getName());
        profileSave.setActive(profile.getActive());
        profileSave.setDescription(profile.getDescription());

        List<ProfileResource> existingResources =
                profileSave.getProfileResources();

        List<ProfileResource> incomingResources =
                profile.getProfileResources();

        Map<UUID, ProfileResource> existingByResourceId =
                existingResources.stream()
                        .collect(Collectors.toMap(
                                resource -> resource.getResource().getId(),
                                Function.identity()
                        ));

        // Atualiza ou adiciona
        for (ProfileResource incoming : incomingResources) {

            UUID resourceId = incoming.getResource().getId();

            ProfileResource existing =
                    existingByResourceId.get(resourceId);

            if (existing != null) {

                // NÃO troca o objeto.
                // Mantém o ProfileResource que já está gerenciado pelo Hibernate.

                existing.setResource(incoming.getResource());
                existing.setPermissions(incoming.getPermissions());

            } else {

                // É um novo ProfileResource
                incoming.setProfile(profileSave);

                existingResources.add(incoming);
            }
        }

        // Remove os que não vieram na requisição
        existingResources.removeIf(existing ->
                incomingResources.stream()
                        .noneMatch(incoming ->
                                incoming.getResource().getId()
                                        .equals(existing.getResource().getId())
                        )
        );

        return this.repository.save(profileSave);
    }

}
