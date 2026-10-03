package br.com.digidatasistemas.starterPackage.service;

import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;

import java.util.List;

public interface IDashboardService {

    List<DashboardResponse> listar();
}
