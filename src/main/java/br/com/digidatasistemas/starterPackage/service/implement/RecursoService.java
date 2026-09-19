package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.repository.RecursoRepository;
import br.com.digidatasistemas.starterPackage.service.IRecursoService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecursoService extends CrudService<Recurso, UUID>
        implements IRecursoService<Recurso> {

    public RecursoService(RecursoRepository repository) {
        super(repository);
    }

    @Override
    public Recurso create(Recurso recurso) {
        recurso.setChave(recurso.getNome().toUpperCase().trim());
        return super.create(recurso);
    }
}
