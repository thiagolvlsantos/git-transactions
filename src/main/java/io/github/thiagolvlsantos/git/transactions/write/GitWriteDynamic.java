package io.github.thiagolvlsantos.git.transactions.write;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("serial")
public class GitWriteDynamic implements Serializable {

	@Builder.Default
	private String value = "";

	@Builder.Default
	private boolean watcher = true;

	@Builder.Default
	GitWriteDirDynamic[] values = new GitWriteDirDynamic[0];

	public String value() {
		return getValue();
	}

	public boolean watcher() {
		return isWatcher();
	}

	public GitWriteDirDynamic[] values() {
		return getValues();
	}
}