package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.model.Recurso;
import br.com.digidatasistemas.starterPackage.repository.RecursoRepository;
import br.com.digidatasistemas.starterPackage.service.IRecursoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class RecursoService extends CrudService<Recurso, UUID>
        implements IRecursoService<Recurso> {

    private final RecursoRepository repository;

    public RecursoService(RecursoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Recurso create(Recurso recurso) {
        recurso.setChave(recurso.getNome().toUpperCase().trim());
        return super.create(recurso);
    }

    @Override
    protected Set<String> updatableProperties() {
        return Set.of("nome", "descricao", "ativo");
    }

    @Override
    public DashboardResponse getDashboardData() {
        var dashboardResponse = repository.buscarDadosDashboard();
        dashboardResponse.setNome("Recursos");
        return dashboardResponse;
    }
}
