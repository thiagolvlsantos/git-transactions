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
public class GitReadDirDynamic implements Serializable, IGitAnnotation {

	private String value;

	public String value() {
		return getValue();
	}
}