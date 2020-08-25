package com.thiagolvlsantos.gitt.provider;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.config.GittConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitProvider implements IGitProvider {

	private @Autowired GittConfig config;
	private Map<String, Git> gits = new HashMap<>();

	private String property(String group, String name) {
		String tmp = config.get(group + "." + name);
		if (tmp == null) {
			return config.get(name);
		}
		return tmp;
	}

	private String local(String group) {
		return String.format(property(group, "local"), group);
	}

	private String remote(String group) {
		return String.format(property(group, "remote"), group);
	}

	@Override
	public File directory(String group) {
		return new File(local(group));
	}

	@Override
	public String normalize(String group, String filename) {
		String prefix = local(group);
		prefix = prefix.replace("\\", "/");
		return filename == null ? null : filename.replace("\\", "/").replace(prefix + "/", "");
	}

	@Override
	public CredentialsProvider credentials(String group) {
		if (log.isInfoEnabled()) {
			log.info("credentials({})", group);
		}
		return new UsernamePasswordCredentialsProvider(property(group, "user"), property(group, "password"));
	}

	@Override
	public Git git(String group) throws GitAPIException {
		return this.gits.computeIfAbsent(group, (k) -> {
			try {
				return instance(k);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}

	@SuppressWarnings("serial")
	private Git instance(String group) throws Exception {
		File dir = directory(group);
		File local = new File(dir, ".git");
		String remote = remote(group);
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
		return git.pull().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public PullResult pullWrite(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("pullWrite({})", group);
		}
		return git.pull().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public RevCommit commit(String group, String msg) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("commit({}):{}", group, msg);
		}
		return git.commit().setAuthor("thiago.santos", "thiagolvlsantos@gmail.com").setMessage(msg).call();
	}

	@Override
	public Iterable<PushResult> push(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("push({})", group);
		}
		return git.push().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public Iterable<PushResult> pushWrite(String group) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("pushWrite({})", group);
		}
		return git.push().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public void clean(String group) throws GitAPIException {
		File dir = directory(group);
		if (log.isInfoEnabled()) {
			log.info("clean({}):{}", group, dir);
		}
	}

	@Override
	public Iterable<RevCommit> log(String group, String path) throws GitAPIException {
		Git git = git(group);
		if (log.isInfoEnabled()) {
			log.info("log({}):{}", group, path);
		}
		return git.log().call();
	}
}