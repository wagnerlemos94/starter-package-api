package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.exception.BusinessException;
import br.com.digidatasistemas.starterPackage.model.Perfil;
import br.com.digidatasistemas.starterPackage.model.PerfilRecurso;
import br.com.digidatasistemas.starterPackage.repository.PerfilRepository;
import br.com.digidatasistemas.starterPackage.service.IPerfilService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.UUID;

import static br.com.digidatasistemas.starterPackage.constrants.Constrants.MSG_RECURSO_JA_EXITESNTE;

@Service
public class PerfilService extends CrudService<Perfil, UUID> implements IPerfilService<Perfil> {

    private PerfilRepository repository;

    public PerfilService(PerfilRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Perfil create(Perfil perfil) {
        if(exitePerfilComEsseNome(perfil.getNome())){
            throw new BusinessException(MSG_RECURSO_JA_EXITESNTE + " com esse nome: " + perfil.getNome());
        }
        perfil.setChave(perfil.getNome().toUpperCase());
        return repository.save(perfil);
    }

    public boolean exitePerfilComEsseNome(String nome) {
        return repository.existsByNome(nome);
    }

    public Perfil update(UUID id, Perfil perfil) {

        Perfil perfilSave = this.findById(id);

        perfilSave.setNome(perfil.getNome());
        perfilSave.setAtivo(perfil.getAtivo());
        perfilSave.setDescricao(perfil.getDescricao());

        List<PerfilRecurso> existingResources =
                perfilSave.getPerfilRecursos();

        List<PerfilRecurso> incomingResources =
                perfil.getPerfilRecursos();

        Map<UUID, PerfilRecurso> existingByResourceId =
                existingResources.stream()
                        .collect(Collectors.toMap(
                                resource -> resource.getRecurso().getId(),
                                Function.identity()
                        ));

        // Atualiza ou adiciona
        for (PerfilRecurso incoming : incomingResources) {

            UUID resourceId = incoming.getRecurso().getId();

            PerfilRecurso existing =
                    existingByResourceId.get(resourceId);

            if (existing != null) {

                // NÃO troca o objeto.
                // Mantém o ProfileResource que já está gerenciado pelo Hibernate.

                existing.setRecurso(incoming.getRecurso());
                existing.setPermissoes(incoming.getPermissoes());

            } else {

                // É um novo ProfileResource
                incoming.setPerfil(perfilSave);

                existingResources.add(incoming);
            }
        }

        // Remove os que não vieram na requisição
        existingResources.removeIf(existing ->
                incomingResources.stream()
                        .noneMatch(incoming ->
                                incoming.getRecurso().getId()
                                        .equals(existing.getRecurso().getId())
                        )
        );

        return this.repository.save(perfilSave);
    }

}
