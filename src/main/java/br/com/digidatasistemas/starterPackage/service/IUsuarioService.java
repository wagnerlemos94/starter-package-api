package br.com.digidatasistemas.starterPackage.service;

import br.com.digidata.crud.service.ICrudService;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface IUsuarioService<T> extends ICrudService<T, UUID> {
}
