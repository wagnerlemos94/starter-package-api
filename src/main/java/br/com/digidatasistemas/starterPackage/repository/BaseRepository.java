package br.com.digidatasistemas.starterPackage.repository;

import br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    @Query(value = """
    SELECT new br.com.digidatasistemas.starterPackage.controller.dto.response.DashboardResponse(
        COUNT(e),
        COALESCE(SUM(CASE WHEN e.ativo = true THEN 1 ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN e.ativo = false THEN 1 ELSE 0 END), 0)
    )
    FROM #{#entityName} e
    """)
    DashboardResponse buscarDadosDashboard();

}
