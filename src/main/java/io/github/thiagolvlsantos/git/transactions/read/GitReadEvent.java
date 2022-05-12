package io.github.thiagolvlsantos.git.transactions.read;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
@SuppressWarnings("serial")
public class GitReadEvent extends ApplicationEvent {

	private GitReadDynamic dynamic;
	private EGitRead type;
	private transient List<GitCommitValue> commits;
	private transient Object result;
	private Throwable error;

	public GitReadEvent(Object source, GitReadDynamic dynamic, EGitRead type, List<GitCommitValue> commits) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
		this.commits = commits;
	}

	public GitReadEvent(Object source, GitReadDynamic dynamic, EGitRead type, Object result) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
		this.result = result;
	}

	public GitReadEvent(Object source, GitReadDynamic dynamic, EGitRead type, Throwable error) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
		this.error = error;
	}
}
