package br.com.digidatasistemas.starterPackage.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "perfil_recurso",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_perfil_recurso",
                        columnNames = {"perfil_id", "recurso_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "perfil_id",
            nullable = false
    )
    private Perfil perfil;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "recurso_id",
            nullable = false
    )
    private Recurso recurso;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "perfil_recurso_permissao",
            joinColumns = @JoinColumn(name = "perfil_recurso_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id")
    )
    @Builder.Default
    private List<Permissao> permissoes = new ArrayList<>();
}