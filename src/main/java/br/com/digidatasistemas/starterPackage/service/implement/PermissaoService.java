package br.com.digidatasistemas.starterPackage.service.implement;

import br.com.digidata.crud.service.CrudService;
import br.com.digidatasistemas.starterPackage.model.Permissao;
import br.com.digidatasistemas.starterPackage.repository.PermissaoRepository;
import br.com.digidatasistemas.starterPackage.service.IPermissaoService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PermissaoService extends CrudService<Permissao, UUID> implements IPermissaoService<Permissao> {

    public PermissaoService(PermissaoRepository repository) {
        super(repository);
    }
}
