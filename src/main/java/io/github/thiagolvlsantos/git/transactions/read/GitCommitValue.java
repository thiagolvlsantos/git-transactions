package io.github.thiagolvlsantos.git.transactions.read;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitCommitValue {
	private GitCommit annotation;
	private Object value;
}