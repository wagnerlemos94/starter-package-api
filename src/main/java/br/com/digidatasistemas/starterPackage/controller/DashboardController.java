package br.com.digidatasistemas.starterPackage.controller;

import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import br.com.digidatasistemas.starterPackage.security.permissao.RecursoPermissao;
import br.com.digidatasistemas.starterPackage.service.IDashboardService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("dashboard")
@RecursoPermissao("DASHBOARD")
@RestController
@AllArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @PreAuthorize("hasAuthority('DASHBOARD:VIEW')")
    @GetMapping
    public List<DashboardResponse> listar() {
        return dashboardService.listar();
    }

}
