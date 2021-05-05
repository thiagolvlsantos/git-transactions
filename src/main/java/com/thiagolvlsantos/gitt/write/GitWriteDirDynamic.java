package com.thiagolvlsantos.gitt.write;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitWriteDirDynamic {

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