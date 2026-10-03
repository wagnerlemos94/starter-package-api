package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.repository.PermissaoRepository;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class PermissaoService extends CrudService<Permissao, UUID> implements IPermissaoService<Permissao> {

    private final PermissaoRepository repository;
    public PermissaoService(PermissaoRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Transactional
    @Override
    public Permissao create(Permissao permissao) {
        permissao.setChave(permissao.getChave().toUpperCase());
        return super.create(permissao);
    }

    @Override
    protected Set<String> updatableProperties() {
        return Set.of("nome", "descricao", "ativo");
    }

    @Override
    public DashboardResponse getDashboardData() {
        var DashboardResponse = repository.buscarDadosDashboard();
        DashboardResponse.setNome("Permissões");
        return DashboardResponse;
    }
}
