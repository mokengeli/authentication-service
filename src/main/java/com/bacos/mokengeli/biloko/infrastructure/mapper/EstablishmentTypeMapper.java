package com.bacos.mokengeli.biloko.infrastructure.mapper;


import com.bacos.mokengeli.biloko.application.domain.model.DomainEstablishmentType;
import com.bacos.mokengeli.biloko.infrastructure.model.EstablishmentType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EstablishmentTypeMapper {

    public EstablishmentType toEntity(DomainEstablishmentType domain) {
        if (domain == null) return null;
        return EstablishmentType.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .label(domain.getLabel())
                .description(domain.getDescription())
                .build();
    }

    public DomainEstablishmentType toDomain(EstablishmentType entity) {
        if (entity == null) return null;
        return DomainEstablishmentType.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .label(entity.getLabel())
                .description(entity.getDescription())
                .build();
    }
}
