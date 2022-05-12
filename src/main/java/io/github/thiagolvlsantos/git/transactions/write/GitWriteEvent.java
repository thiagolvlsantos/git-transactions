package io.github.thiagolvlsantos.git.transactions.write;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@ToString
public class GitWriteEvent extends ApplicationEvent {

	private GitWriteDynamic dynamic;
	private EGitWrite type;
	private transient Object result;
	private Throwable error;

	public GitWriteEvent(Object source, GitWriteDynamic dynamic, EGitWrite type) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
	}

	public GitWriteEvent(Object source, GitWriteDynamic dynamic, EGitWrite type, Object result) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
		this.result = result;
	}

	public GitWriteEvent(Object source, GitWriteDynamic dynamic, EGitWrite type, Throwable error) {
		super(source);
		this.dynamic = dynamic;
		this.type = type;
		this.error = error;
	}
}
