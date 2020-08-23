package com.thiagolvlsantos.gitt.read;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@SuppressWarnings("serial")
@Getter
public class GitReadEvent extends ApplicationEvent {

	private GitRead annotation;
	private EGitRead type;
	private Object result;
	private Throwable error;

	public GitReadEvent(Object source, GitRead annotation, EGitRead type) {
		super(source);
		this.annotation = annotation;
		this.type = type;
	}

	public GitReadEvent(Object source, GitRead annotation, EGitRead type, Object result) {
		super(source);
		this.annotation = annotation;
		this.type = type;
		this.result = result;
	}

	public GitReadEvent(Object source, GitRead annotation, EGitRead type, Throwable error) {
		super(source);
		this.annotation = annotation;
		this.type = type;
		this.error = error;
	}
}
