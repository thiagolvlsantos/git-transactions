package io.github.thiagolvlsantos.git.transactions.provider;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;

public interface IGitProvider {

	void setTimestamp(String group, Long timestamp);

	void setCommit(String group, String commit);

	File directoryRead(String group);

	File directoryWrite(String group);

	String normalizeRead(String group, String filePattern);

	String normalizeWrite(String group, String filePattern);

	CredentialsProvider credentials(String group);

	Git gitRead(String group) throws GitAPIException;

	Git gitWrite(String group) throws GitAPIException;

	String keyRead(String group);

	String keyWrite(String group);

	PullResult pullRead(String group) throws GitAPIException;

	PullResult pullWrite(String group) throws GitAPIException;

	RevCommit commitRead(String group, String msg) throws GitAPIException;

	RevCommit commitWrite(String group, String msg) throws GitAPIException;

	Iterable<PushResult> pushRead(String group) throws GitAPIException;

	Iterable<PushResult> pushWrite(String group) throws GitAPIException;

	void cleanRead(String group) throws GitAPIException;

	void cleanWrite(String group) throws GitAPIException;

	Iterable<RevCommit> logRead(String group, String path, Integer skip, Integer max) throws GitAPIException;

	Iterable<RevCommit> logWrite(String group, String path, Integer skip, Integer max) throws GitAPIException;

}