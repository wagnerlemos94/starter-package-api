package br.com.digidatasistemas.starterPackage.service;

import br.com.digidata.crud.service.ICrudService;
import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.model.IDashboard;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface IPerfilService<T> extends ICrudService<T, UUID>, IDashboard {

    boolean existsById(UUID id);
}
