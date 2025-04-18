package com.bacos.mokengeli.biloko.infrastructure.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String name;  // Nom du tenant (restaurant/lounge)

    @Column(nullable = false, unique = true)
    private String code;

    private String address;  // Adresse du tenant

    @Column(unique = true)
    private String email;  // Email du tenant

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
