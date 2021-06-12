package io.github.thiagolvlsantos.git.transactions.write;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@SuppressWarnings("serial")
public class GitWriteDirDynamic implements Serializable {

	private String value;

	@Builder.Default
	private boolean watcher = true;

	public String value() {
		return value;
	}

	public boolean watcher() {
		return isWatcher();
	}
}