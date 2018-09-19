package com.hathor.docs.auth;

import lombok.Getter;

@Getter
public enum UserTypes {
	FILLIN_ADMIN(1), FILLIN_USER(2), COMPANY_CONTACT(3), FILLIN_MANAGER(4);

	private Integer id;

	UserTypes(Integer id) {
		this.id = id;
	}
}
