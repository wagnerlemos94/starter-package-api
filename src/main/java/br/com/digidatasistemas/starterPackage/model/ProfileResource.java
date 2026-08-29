package br.com.digidatasistemas.starterPackage.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "profile_resources",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_profile_resource",
                        columnNames = {"profile_id", "resource_id"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "profile_id",
            nullable = false
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "resource_id",
            nullable = false
    )
    private Resource resource;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_resource_permissions",
            joinColumns = @JoinColumn(name = "profile_resource_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
}