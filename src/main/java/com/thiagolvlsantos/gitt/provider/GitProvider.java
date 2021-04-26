package com.thiagolvlsantos.gitt.provider;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.springframework.util.FileSystemUtils;

import com.thiagolvlsantos.gitt.config.GittConfig;
import com.thiagolvlsantos.gitt.id.SessionIdHolderHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GitProvider implements IGitProvider {

	private static final String REPO_READ = "read";
	private static final String REPO_WRITE = "write";
	private static final String REPO_REMOTE = "remote";
	private static final String REPO_USER = "user";
	private static final String REPO_PASSWORD = "password";

	private @Autowired ApplicationContext context;
	private Map<String, Git> gitsRead = new ConcurrentHashMap<>();
	private Map<String, Git> gitsWrite = new ConcurrentHashMap<>();

	private String property(String group, String name) {
		GittConfig config = context.getBean(GittConfig.class);
		String tmp = config.get(group + "." + name);
		if (tmp == null) {
			return config.get(name);
		}
		return tmp;
	}

	private String read(String group) {
		return String.format(property(group, REPO_READ), group);
	}

	private String write(String group) {
		return String.format(property(group, REPO_WRITE), group);
	}

	private String remote(String group) {
		return String.format(property(group, REPO_REMOTE), group);
	}

	@Override
	public File directoryRead(String group) {
		return new File(read(group));
	}

	@Override
	public File directoryWrite(String group) {
		return new File(new File(write(group)), SessionIdHolderHelper.holder(context).current());
	}

	@Override
	public String normalizeRead(String group, String filename) {
		return normalize(read(group), filename);
	}

	@Override
	public String normalizeWrite(String group, String filename) {
		return normalize(write(group), filename);
	}

	private String normalize(String prefix, String filename) {
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
	public Git gitRead(String group) throws GitAPIException {
		String key = keyRead(group);
		return this.gitsRead.computeIfAbsent(key, (k) -> {
			try {
				return instance(group, directoryRead(group));
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		});
	}

	private String keyRead(String group) {
		return group;
	}

	@Override
	public Git gitWrite(String group) throws GitAPIException {
		String key = keyWrite(group);
		return this.gitsWrite.computeIfAbsent(key, (k) -> {
			try {
				return instance(group, directoryWrite(group));
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		});
	}

	private String keyWrite(String group) {
		return group + "_" + SessionIdHolderHelper.holder(context).current();
	}

	@SuppressWarnings("serial")
	private Git instance(String group, File local) throws GitAPIException {
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
	public PullResult pullRead(String group) throws GitAPIException {
		if (log.isInfoEnabled()) {
			log.info("pullRead({})", group);
		}
		return pull(group, gitRead(group));
	}

	private PullResult pull(String group, Git git) throws GitAPIException {
		return git.pull().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public PullResult pullWrite(String group) throws GitAPIException {
		if (log.isInfoEnabled()) {
			log.info("pullWrite({})", group);
		}
		PullResult result = pullRead(group);
		try {
			FileSystemUtils.copyRecursively(directoryRead(group), directoryWrite(group));
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public RevCommit commitRead(String group, String msg) throws GitAPIException {
		return commit(group, msg, gitRead(group));
	}

	@Override
	public RevCommit commitWrite(String group, String msg) throws GitAPIException {
		return commit(group, msg, gitWrite(group));
	}

	private RevCommit commit(String group, String msg, Git git) throws GitAPIException {
		IGitAudit audit = audit();
		if (log.isInfoEnabled()) {
			log.info("commit({}): {}, {} -> {}", group, audit.username(), audit.email(), msg);
		}
		return git.commit().setAuthor(audit.username(), audit.email()).setMessage(msg).call();
	}

	private IGitAudit audit() {
		IGitAudit audit;
		try {
			audit = context.getBean(IGitAudit.class);
		} catch (NoSuchBeanDefinitionException e) {
			audit = IGitAudit.INSTANCE;
		}
		return audit;
	}

	@Override
	public Iterable<PushResult> pushRead(String group) throws GitAPIException {
		if (log.isInfoEnabled()) {
			log.info("pushRead({}) NOP", group);
		}
		return new LinkedList<>();// push(group, gitRead(group));
	}

	@Override
	public Iterable<PushResult> pushWrite(String group) throws GitAPIException {
		if (log.isInfoEnabled()) {
			log.info("pushWrite({})", group);
		}
		return push(group, gitWrite(group));
	}

	private Iterable<PushResult> push(String group, Git git) throws GitAPIException {
		return git.push().setCredentialsProvider(credentials(group)).call();
	}

	@Override
	public void cleanRead(String group) throws GitAPIException {
		String key = keyRead(group);
		File dir = directoryRead(group);
		if (log.isInfoEnabled()) {
			log.info("cleanRead({}):{} NOP", key, dir);
		}
//		Git git = this.gitsRead.remove(key);
//		git.close();
	}

	@Override
	public void cleanWrite(String group) throws GitAPIException {
		String key = keyWrite(group);
		File dir = directoryWrite(group);
		if (log.isInfoEnabled()) {
			log.info("cleanWrite({}):{}", key, dir);
		}
		Git git = this.gitsWrite.remove(key);
		git.close();
		try {
			if (FileSystemUtils.deleteRecursively(dir.toPath())) {
				if (log.isInfoEnabled()) {
					log.info("cleanWrite failure.");
				}
			}
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}

	}

	@Override
	public Iterable<RevCommit> logRead(String group, String path) throws GitAPIException {
		return log(group, path, gitRead(group));
	}

	@Override
	public Iterable<RevCommit> logWrite(String group, String path) throws GitAPIException {
		return log(group, path, gitWrite(group));
	}

	private Iterable<RevCommit> log(String group, String path, Git git) throws GitAPIException {
		if (log.isInfoEnabled()) {
			log.info("log({}):{}", group, path);
		}
		return git.log().call();
	}
}