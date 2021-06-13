package io.github.thiagolvlsantos.git.transactions.read;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@SuppressWarnings("serial")
public class GitReadDirDynamic implements Serializable {

	private String value;

	public String value() {
		return getValue();
	}
}