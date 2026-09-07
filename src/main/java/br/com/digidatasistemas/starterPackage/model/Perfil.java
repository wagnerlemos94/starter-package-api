package br.com.digidatasistemas.starterPackage.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "perfil")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String chave;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = Boolean.TRUE;

    @OneToMany(
            mappedBy = "perfil",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PerfilRecurso> perfilRecursos = new ArrayList<>();
}
