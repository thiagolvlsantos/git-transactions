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
public class GitWriteDynamic implements Serializable {

	@Builder.Default
	private String value = "";

	@Builder.Default
	GitWriteDirDynamic[] values = new GitWriteDirDynamic[0];

	@Builder.Default
	private boolean watcher = true;

	public String value() {
		return getValue();
	}

	public GitWriteDirDynamic[] values() {
		return getValues();
	}

	public boolean watcher() {
		return isWatcher();
	}
}