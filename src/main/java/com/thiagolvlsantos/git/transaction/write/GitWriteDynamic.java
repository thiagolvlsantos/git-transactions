package com.thiagolvlsantos.git.transaction.write;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitWriteDynamic {

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