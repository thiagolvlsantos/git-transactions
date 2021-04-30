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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.thiagolvlsantos.gitt.config.GittConfig;
import com.thiagolvlsantos.gitt.id.SessionIdHolderHelper;
import com.thiagolvlsantos.gitt.scope.AspectScoped;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AspectScoped
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
		return normalize(write(group) + File.separator + SessionIdHolderHelper.holder(context).current(), filename);
	}

	private String normalize(String prefix, String filename) {
		prefix = prefix.replace("\\", "/");
		return filename == null ? null : filename.replace("\\", "/").replace(prefix + "/", "");
	}

	@Override
	public CredentialsProvider credentials(String group) {
		if (log.isDebugEnabled()) {
			log.debug("credentials({})", group);
		}
		return new UsernamePasswordCredentialsProvider(property(group, REPO_USER), property(group, REPO_PASSWORD));
	}

	@Override
	public Git gitRead(String group) throws GitAPIException {
		return gitReadSilent(group, false);
	}

	public Git gitReadSilent(String group, boolean silent) throws GitAPIException {
		String key = keyRead(group);
		Git instance = this.gitsRead.computeIfAbsent(key, (k) -> {
			try {
				return instance(group, directoryRead(group), silent);
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		});
		if (!silent && log.isDebugEnabled()) {
			log.debug("gitRead.keys: {}", this.gitsRead.keySet());
		}
		return instance;
	}

	public String keyRead(String group) {
		return group;
	}

	@Override
	public Git gitWrite(String group) throws GitAPIException {
		String key = keyWrite(group);
		Git instance = this.gitsWrite.computeIfAbsent(key, (k) -> {
			try {
				return instance(group, directoryWrite(group), false);
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
				throw new RuntimeException(e);
			}
		});
		if (log.isDebugEnabled()) {
			log.debug("gitWrite.keys: {}", this.gitsWrite.keySet());
		}
		return instance;
	}

	public String keyWrite(String group) {
		return group + "_" + SessionIdHolderHelper.holder(context).current();
	}

	@SuppressWarnings("serial")
	private Git instance(String group, File local, boolean silent) throws GitAPIException {
		String remote = remote(group);
		if (!silent && log.isInfoEnabled()) {
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
		return pull(group, gitRead(group), "pullRead");
	}

	private PullResult pull(String group, Git git, String msg) throws GitAPIException {
		long time = System.currentTimeMillis();
		PullResult pull = git.pull().setCredentialsProvider(credentials(group)).call();
		if (log.isDebugEnabled()) {
			log.debug(msg + "({}) time={}", group, System.currentTimeMillis() - time);
		}
		return pull;
	}

	@Override
	public PullResult pullWrite(String group) throws GitAPIException {
		pullReadSilent(group);
		long time = System.currentTimeMillis();
		try {
			FileSystemUtils.copyRecursively(directoryRead(group), directoryWrite(group));
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RuntimeException(e);
		}
		if (log.isDebugEnabled()) {
			log.debug("copy({}) time={}", group, System.currentTimeMillis() - time);
		}
		return pull(group, gitWrite(group), "pullWrite");
	}

	public PullResult pullReadSilent(String group) throws GitAPIException {
		return pull(group, gitReadSilent(group, true), "pullReadSilent");
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
		IGitAudit audit = GitAuditHelper.audit(context);
		long time = System.currentTimeMillis();
		RevCommit call = git.commit().setAuthor(audit.username(), audit.email()).setMessage(msg).call();
		if (log.isDebugEnabled()) {
			log.debug("commit({}) time={}: {}, {} -> {}", group, System.currentTimeMillis() - time, audit.username(),
					audit.email(), msg);
		}
		return call;
	}

	@Override
	public Iterable<PushResult> pushRead(String group) throws GitAPIException {
		return new LinkedList<>();
	}

	@Override
	public Iterable<PushResult> pushWrite(String group) throws GitAPIException {
		return push(group, gitWrite(group), "pushWrite");
	}

	private Iterable<PushResult> push(String group, Git git, String msg) throws GitAPIException {
		long time = System.currentTimeMillis();
		Iterable<PushResult> call = git.push().setCredentialsProvider(credentials(group)).call();
		if (log.isDebugEnabled()) {
			log.debug(msg + "({}) time={}", group, System.currentTimeMillis() - time);
		}
		return call;
	}

	@Override
	public void cleanRead(String group) throws GitAPIException {
		long time = System.currentTimeMillis();
		String key = keyRead(group);
		File dir = directoryRead(group);
		Git git = this.gitsRead.remove(key);
		if (git != null) {
			git.close();
		}
		if (log.isDebugEnabled()) {
			log.debug("cleanRead({}):{} NOP time={}", key, dir, System.currentTimeMillis() - time);
		}
	}

	@Override
	public void cleanWrite(String group) throws GitAPIException {
		long time = System.currentTimeMillis();
		String key = keyWrite(group);
		File dir = directoryWrite(group);
		Git git = this.gitsWrite.remove(key);
		if (git != null) {
			git.close();
		}
		boolean delete = delete(dir);
		if (!delete && log.isInfoEnabled()) {
			log.info("Could not delete: {}", dir);
		}
		if (log.isDebugEnabled()) {
			log.debug("cleanWrite(success:{},{}):{} NOP time={}", delete, key, dir, System.currentTimeMillis() - time);
		}
	}

	private boolean delete(File f) {
		boolean ok = true;
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				ok = ok & delete(c);
			}
		}
		return ok & f.delete();
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
		long time = System.currentTimeMillis();
		Iterable<RevCommit> call = git.log().call();
		if (log.isDebugEnabled()) {
			log.debug("log({}):{}, time={}", group, path, System.currentTimeMillis() - time);
		}
		return call;
	}
}