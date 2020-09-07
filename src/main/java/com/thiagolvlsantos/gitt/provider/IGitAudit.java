package com.thiagolvlsantos.gitt.provider;

public interface IGitAudit {

	default String username() {
		return "";
	}

	default String email() {
		return "";
	}

	IGitAudit INSTANCE = new IGitAudit() {
	};
}
