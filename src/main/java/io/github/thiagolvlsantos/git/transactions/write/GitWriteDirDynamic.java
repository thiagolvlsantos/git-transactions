package io.github.thiagolvlsantos.git.transactions.write;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@SuppressWarnings("serial")
@ToString
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