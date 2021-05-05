package com.thiagolvlsantos.gitt.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitReadDirDynamic {

	@Builder.Default
	private String value = "";

	public String value() {
		return getValue();
	}
}