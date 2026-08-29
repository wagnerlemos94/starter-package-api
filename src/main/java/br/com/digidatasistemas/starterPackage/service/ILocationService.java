package br.com.digidatasistemas.starterPackage.service;

import java.util.List;
import java.util.UUID;

public interface ILocationService<T>{
    T findByLocation(UUID id);
    T create(T entity);
}
