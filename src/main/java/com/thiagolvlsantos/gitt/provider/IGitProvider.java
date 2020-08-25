package com.thiagolvlsantos.gitt.provider;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;

public interface IGitProvider {

	File directory(String group);

	String normalize(String group, String filePattern);

	CredentialsProvider credentials(String group);

	Git git(String group) throws GitAPIException;

	PullResult pull(String group) throws GitAPIException;

	PullResult pullWrite(String group) throws GitAPIException;

	RevCommit commit(String group, String msg) throws GitAPIException;

	Iterable<PushResult> push(String group) throws GitAPIException;

	Iterable<PushResult> pushWrite(String group) throws GitAPIException;

	void clean(String group) throws GitAPIException;

	Iterable<RevCommit> log(String group, String path) throws GitAPIException;
}