package com.thiagolvlsantos.gitt.write;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@ToString
public class GitWriteEvent extends ApplicationEvent {

	private GitWriteDynamic annotation;
	private EGitWrite type;
	private Object result;
	private Throwable error;

	public GitWriteEvent(Object source, GitWriteDynamic annotation, EGitWrite type) {
		super(source);
		this.annotation = annotation;
		this.type = type;
	}

	public GitWriteEvent(Object source, GitWriteDynamic annotation, EGitWrite type, Object result) {
		super(source);
		this.annotation = annotation;
		this.type = type;
		this.result = result;
	}

	public GitWriteEvent(Object source, GitWriteDynamic annotation, EGitWrite type, Throwable error) {
		super(source);
		this.annotation = annotation;
		this.type = type;
		this.error = error;
	}
}
