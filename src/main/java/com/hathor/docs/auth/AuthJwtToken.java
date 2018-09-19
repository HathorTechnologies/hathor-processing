package com.hathor.docs.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.ardas.jwt.JwtData;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthJwtToken implements JwtData {

    private Integer userId;
    private Integer workerId;

	private Integer companyId;

	private String localeKey;
	private String fullName;
	private String xsrfToken;

	private UserTypes userTypeKey;
	private Set<String> userPermissions;
	private Integer departmentId;
	private Set<Integer> projectsIds;

    @Override
    public String getXsrfToken() {
        return xsrfToken;
    }
}
