package br.com.digidatasistemas.starterPackage.controller.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DashboardResponse {

    private String nome;
    private String descricao;
    private Long total;
    private Long ativos;
    private Long inativos;

    public DashboardResponse(Long total, Long ativos, Long inativos) {
        this.inativos = inativos;
        this.ativos = ativos;
        this.total = total;
    }
}
