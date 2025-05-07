package com.bacos.mokengeli.biloko.infrastructure.mapper;

import com.bacos.mokengeli.biloko.application.domain.model.DomainSubscriptionPlan;
import com.bacos.mokengeli.biloko.infrastructure.model.SubscriptionPlan;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SubscriptionPlanMapper {

    public SubscriptionPlan toEntity(DomainSubscriptionPlan domain) {
        if (domain == null) return null;
        return SubscriptionPlan.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .label(domain.getLabel())
                .monthlyPrice(domain.getMonthlyPrice())
                .features(domain.getFeatures())
                .build();
    }

    public DomainSubscriptionPlan toDomain(SubscriptionPlan entity) {
        if (entity == null) return null;
        return DomainSubscriptionPlan.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .label(entity.getLabel())
                .monthlyPrice(entity.getMonthlyPrice())
                .features(entity.getFeatures())
                .build();
    }
}
