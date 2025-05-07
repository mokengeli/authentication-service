package com.bacos.mokengeli.biloko.application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Plan d’abonnement (starter, premium…).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainSubscriptionPlan {
    private Long id;
    private String code;
    private String label;
    private BigDecimal monthlyPrice;
    private String features; // JSON brut ou sérialisé
}
