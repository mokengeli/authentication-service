package com.bacos.mokengeli.biloko.application.domain.model;

import com.bacos.mokengeli.biloko.application.domain.PlatformTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedUser {
    private UUID jti; // session unique de connexion
    private String tenantCode;
    private String employeeNumber;
    private PlatformTypeEnum platformTypeEnum;
    private List<String> roles;
    private List<String> permissions;

}
