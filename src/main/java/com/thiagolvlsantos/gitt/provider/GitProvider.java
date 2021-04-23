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
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.thiagolvlsantos.gitt.config.GittConfig;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitProvider implements IGitProvider {

	private static final String REPO_LOCAL = "local";
	private static final String REPO_REMOTE = "remote";
	private static final String REPO_USER = "user";
	private static final String REPO_PASSWORD = "password";

	private @Autowired ApplicationContext context;
	private Map<String, Git> gits = new HashMap<>();

	private String property(String group, String name) {
		GittConfig config = context.getBean(GittConfig.class);
		String tmp = config.get(group + "." + name);
		if (tmp == null) {
			return config.get(name);
		}
		return tmp;
	}

	private String local(String group) {
		return String.format(property(group, REPO_LOCAL), group);
	}

	private String remote(String group) {
		return String.format(property(group, REPO_REMOTE), group);
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
		return new UsernamePasswordCredentialsProvider(property(group, REPO_USER), property(group, REPO_PASSWORD));
	}

	@Override
	public Git git(String group) throws GitAPIException {
		return this.gits.computeIfAbsent(group, (k) -> {
			try {
				return instance(k);
			} catch (Exception e) {
				if(log.isDebugEnabled()) {
					log.debug(e.getMessage(),e);
				}
				throw new RuntimeException(e);
			}
		});
	}

	@SuppressWarnings("serial")
	private Git instance(String group) throws Exception {
		File local = directory(group);
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
		IGitAudit audit;
		try {
			audit = context.getBean(IGitAudit.class);
		} catch (NoSuchBeanDefinitionException e) {
			audit = IGitAudit.INSTANCE;
		}
		if (log.isInfoEnabled()) {
			log.info("commit({}): {}, {} -> {}", group, audit.username(), audit.email(), msg);
		}
		return git.commit().setAuthor(audit.username(), audit.email()).setMessage(msg).call();
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
	public void cleanWrite(String group) throws GitAPIException {
		File dir = directory(group);
		if (log.isInfoEnabled()) {
			log.info("cleanWrite({}):{}", group, dir);
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