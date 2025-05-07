package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Type d’enseigne (restaurant, bar, lounge, platform…).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEstablishmentType {
    private Long   id;
    private String code;
    private String label;
    private String description;
}
