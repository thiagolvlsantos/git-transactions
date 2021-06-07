package io.github.thiagolvlsantos.git.transactions.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitReadDynamic {

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