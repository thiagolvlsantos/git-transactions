package com.thiagolvlsantos.gitt.provider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitProvider implements IGitProvider {

	private String remoteRepository = "https://github.com/thiagolvlsantos/%s.git";
	private String localRepository = "data/%s";
	private Map<String, Git> gits = new HashMap<>();

	@Override
	public File directory(String group) {
		return new File(String.format(localRepository, group));
	}

	@Override
	public String normalize(String filePattern) {
		String prefixo = localRepository;
		prefixo = prefixo.replace("\\", "/");
		return filePattern == null ? null : filePattern.replace("\\", "/").replace(prefixo + "/", "");
	}

	@Override
	public CredentialsProvider credentials(String group) {
		if (log.isInfoEnabled()) {
			log.info("credentials({})", group);
		}
		return new UsernamePasswordCredentialsProvider("thiagolvlsantos", "XYZ");
	}

	@Override
	public Git git(String group) throws GitAPIException {
		return this.gits.computeIfAbsent(group, (k) -> {
			try {
				return instance(k);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	@SuppressWarnings("serial")
	private Git instance(String group) throws Exception {
		File local = new File(directory(group), ".git");
		String remote = String.format(remoteRepository, group);
		if (log.isInfoEnabled()) {
			log.info("git({}): local:{}, remote:{}", group, local, remote);
		}
		try {
			return Git.open(local);
		} catch (RepositoryNotFoundException e) {
			return Git.cloneRepository()//
					.setCredentialsProvider(credentials(group))//
					.setURI(remote)//
					.setDirectory(local).call();
		} catch (IOException e) {
			throw new GitAPIException(e.getMessage(), e) {
			};
		}
	}

	@Override
	public PullResult pull(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("pull({})", group);
		}
		return null;
	}

	@Override
	public PullResult pullWrite(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("pullWrite({})", group);
		}
		return null;
	}

	@Override
	public RevCommit commit(String group, String msg) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("commit({}):{}", group, msg);
		}
		return null;
	}

	@Override
	public Iterable<PushResult> push(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("push({})", group);
		}
		return null;
	}

	@Override
	public Iterable<PushResult> pushWrite(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("pushWrite({})", group);
		}
		return null;
	}

	@Override
	public void clean(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("clean({})", group);
		}
	}

	@Override
	public List<RevCommit> log(String group, String path) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("log({}):{}", group, path);
		}
		return null;
	}
}