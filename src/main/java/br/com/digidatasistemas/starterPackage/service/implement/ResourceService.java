package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Resource;
import br.com.digidatasistemas.starterPackage.repository.ResourceRepository;
import br.com.digidatasistemas.starterPackage.service.IResourceService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ResourceService extends CrudService<Resource, UUID>
        implements IResourceService<Resource> {

    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        super(repository);
        this.repository = repository;
    }
}