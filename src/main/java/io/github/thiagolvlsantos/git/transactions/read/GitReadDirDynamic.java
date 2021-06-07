package io.github.thiagolvlsantos.git.transactions.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitReadDirDynamic {

	private String value;

	public String value() {
		return getValue();
	}
}