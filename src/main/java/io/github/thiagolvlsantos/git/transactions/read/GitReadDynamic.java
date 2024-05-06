package io.github.thiagolvlsantos.git.transactions.read;

import java.io.Serializable;

import io.github.thiagolvlsantos.git.transactions.IGitAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@SuppressWarnings("serial")
@ToString
public class GitReadDynamic implements Serializable, IGitAnnotation {

	@Builder.Default
	private String value = "";

	@Builder.Default
	private GitReadDirDynamic[] values = new GitReadDirDynamic[0];

	public String value() {
		return getValue();
	}

	public GitReadDirDynamic[] values() {
		return getValues();
	}
}