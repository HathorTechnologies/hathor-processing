package com.hathor.docs.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.ardas.jwt.JwtData;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthJwtToken implements JwtData {

    private Integer userId;
    private UUID nodeId;
    private String email;
    private String fullName;
    private Integer ownerId;
    private String xsrfToken;
    private String role;
    private Set<String> permissions;
}
