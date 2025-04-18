package com.bacos.mokengeli.biloko.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false, unique = true)
    private String label;  // Nom du rôle (ex: 'admin', 'serveur')

    private String description;  // Description du rôle

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    // Relation Many-to-Many avec Permissions
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",  // Table intermédiaire
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @JsonManagedReference  // Indique le point de départ de la sérialisation
    @EqualsAndHashCode.Exclude  // Exclut du hashCode et equals
    @ToString.Exclude  // Exclut du toString()
    private Set<Permission> permissions;  //

    // Relation Many-to-Many avec Users
    @ManyToMany(mappedBy = "roles")
    @JsonBackReference  // Empêche la sérialisation récursive
    @EqualsAndHashCode.Exclude  // Exclut du hashCode et equals
    @ToString.Exclude  // Exclut du toString()
    private Set<User> users;

}