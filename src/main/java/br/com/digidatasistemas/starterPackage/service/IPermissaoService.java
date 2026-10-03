package br.com.digidatasistemas.starterPackage.service;

import br.com.digidata.crud.service.ICrudService;
import br.com.digidatasistemas.starterPackage.model.IDashboard;

import java.util.UUID;

public interface IPermissaoService<T> extends ICrudService<T, UUID>, IDashboard {
}
