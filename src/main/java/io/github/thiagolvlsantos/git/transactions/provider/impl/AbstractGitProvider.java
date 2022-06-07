package io.github.thiagolvlsantos.git.transactions.provider.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import io.github.thiagolvlsantos.git.transactions.config.GitConfiguration;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.id.impl.SessionIdHolderHelper;
import io.github.thiagolvlsantos.git.transactions.provider.IGitAudit;
import io.github.thiagolvlsantos.git.transactions.provider.IGitAudit.UserInfo;
import io.github.thiagolvlsantos.git.transactions.provider.IGitProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGitProvider implements IGitProvider {

	private static final String REPO_READ = "read";
	private static final String REPO_WRITE = "write";
	private static final String REPO_REMOTE = "remote";
	private static final String REPO_USER = "user";
	private static final String REPO_PASSWORD = "password";

	private @Autowired ApplicationContext context;
	private Map<String, Git> gitsRead = new ConcurrentHashMap<>();
	private Map<String, Git> gitsWrite = new ConcurrentHashMap<>();
	private Map<String, File> gitsCommits = new ConcurrentHashMap<>();

	protected String property(String group, String name) {
		GitConfiguration config = context.getBean(GitConfiguration.class);
		String tmp = config.get(group + "." + name);
		if (tmp == null) {
			return config.get(name);
		}
		return tmp;
	}

	protected String read(String group) {
		return String.format(property(group, REPO_READ), group);
	}

	protected String write(String group) {
		return String.format(property(group, REPO_WRITE), group);
	}

	protected String remote(String group) {
		return String.format(property(group, REPO_REMOTE), group);
	}

	@Override
	public void setTimestamp(String group, Long timestamp) throws GitAPIException {
		if (timestamp != null) {
			String commit = findCommit(group, timestamp);
			if (commit != null) {
				log.info("Commit for ({}) = {}", new Date(timestamp), commit);
				setCommit(group, commit);
			}
		}
	}

	protected String findCommit(String group, Long timestamp) throws GitAPIException {
		TreeMap<Date, RevCommit> commits = commitsBefore(group, timestamp);
		return commits.isEmpty() ? null : commits.lastEntry().getValue().getName();
	}

	protected TreeMap<Date, RevCommit> commitsBefore(String group, Long timestamp) throws GitAPIException {
		TreeMap<Date, RevCommit> result = new TreeMap<>();
		try {
			Date time = new Date(timestamp);
			Git git = gitRead(group);
			Repository repo = git.getRepository();
			try (RevWalk walk = new RevWalk(repo)) {
				walk.markStart(walk.parseCommit(repo.resolve(Constants.HEAD)));
				walk.sort(RevSort.COMMIT_TIME_DESC);
				// walk.setTreeFilter(PathFilter.create(path)); // in case of path is required
				for (RevCommit commit : walk) {
					Date when = commit.getCommitterIdent().getWhen();
					if (when.before(time)) {
						result.put(when, commit);
					}
				}
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
			throw new GitTransactionsException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void setCommit(String group, String commit) {
		if (commit != null) {
			long t = System.currentTimeMillis();
			// if a commit is set read behaves like write, 'repo' is disposable
			try {
				// redo action as write
				pullWrite(group);
				// the target directory should replace 'readDirectory'
				gitsCommits.put(group, directoryWrite(group));
				// the repository
				Git git = gitWrite(group);
				// is reset to the commit state
				Ref call = git.checkout().setName(commit).call();
				log.info("Checkout of (commit={}): time={}, call={}", commit, System.currentTimeMillis() - t,
						call != null ? call.getName() : null);
			} catch (GitAPIException e) {
				log.error("Checkout not performed (commit={}): time={}, error={}", commit,
						System.currentTimeMillis() - t, e.getMessage());
				log.debug(e.getMessage(), e);
				throw new GitTransactionsException(e.getMessage(), e);
			}
		}
	}

	@Override
	public File directoryRead(String group) {
		return gitsCommits.containsKey(group) ? gitsCommits.get(group) : new File(read(group));
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

	protected String normalize(String prefix, String filename) {
		prefix = prefix.replace("\\", "/");
		return filename == null ? null : filename.replace("\\", "/").replace(prefix + "/", "");
	}

	@Override
	public CredentialsProvider credentials(String group) {
		log.debug("credentials({})", group);
		return new UsernamePasswordCredentialsProvider(property(group, REPO_USER), property(group, REPO_PASSWORD));
	}

	@Override
	public Git gitRead(String group) throws GitAPIException {
		return gitReadSilent(group, false);
	}

	public Git gitReadSilent(String group, boolean silent) throws GitAPIException {
		String key = keyRead(group);
		Git instance = this.gitsRead.computeIfAbsent(key, k -> {
			try {
				return instance(group, directoryRead(group), silent);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				throw new GitTransactionsException(e.getMessage(), e);
			}
		});
		if (!silent) {
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
		Git instance = this.gitsWrite.computeIfAbsent(key, k -> {
			try {
				return instance(group, directoryWrite(group), false);
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
				throw new GitTransactionsException(e.getMessage(), e);
			}
		});
		log.debug("gitWrite.keys: {}", this.gitsWrite.keySet());
		return instance;
	}

	public String keyWrite(String group) {
		return group + "_" + SessionIdHolderHelper.holder(context).current();
	}

	@SuppressWarnings("serial")
	protected Git instance(String group, File local, boolean silent) throws GitAPIException {
		String remote = remote(group);
		if (!silent) {
			log.info("{}.git({}): local:{}, remote:{}", getClass().getSimpleName(), group, local, remote);
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

	protected PullResult pull(String group, Git git, String msg) throws GitAPIException {
		long time = System.currentTimeMillis();
		PullResult pull = git.pull().setCredentialsProvider(credentials(group)).call();
		log.debug("{}({}) time={}", msg, group, System.currentTimeMillis() - time);
		return pull;
	}

	@Override
	public PullResult pullWrite(String group) throws GitAPIException {
		pullReadSilent(group);
		long time = System.currentTimeMillis();
		try {
			FileUtils.copyDirectory(directoryRead(group), directoryWrite(group));
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new GitTransactionsException(e.getMessage(), e);
		}
		log.debug("copy({}) time={}", group, System.currentTimeMillis() - time);
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

	protected RevCommit commit(String group, String msg, Git git) throws GitAPIException {
		long time = System.currentTimeMillis();
		IGitAudit audit = GitAuditHelper.audit(context);
		UserInfo author = audit.author();
		UserInfo commiter = audit.committer();
		String text = "Changes made by: " + author.getUser() + "/" + commiter.getUser() + "\n\n" + msg;
		RevCommit call = git.commit()//
				.setAuthor(author.getUser(), author.getEmail())//
				.setCommitter(commiter.getUser(), commiter.getEmail())//
				.setMessage(text).call();
		log.debug("commit({}) time={}: ({}, {}) -> {}", group, System.currentTimeMillis() - time, author, commiter,
				text);
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

	protected Iterable<PushResult> push(String group, Git git, String msg) throws GitAPIException {
		long time = System.currentTimeMillis();
		Iterable<PushResult> call = git.push().setCredentialsProvider(credentials(group)).call();
		log.debug("{}({}) time={}", msg, group, System.currentTimeMillis() - time);
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
		log.debug("cleanRead({}):{} NOP time={}", key, dir, System.currentTimeMillis() - time);

		if (gitsCommits.containsKey(group)) {
			cleanWrite(group);
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
		try {
			FileUtils.deleteDirectory(dir);
			log.debug("cleanWrite(success,{}):{} time={}", key, dir, System.currentTimeMillis() - time);
		} catch (IOException e) {
			log.debug("cleanWrite(error:{},{}):time={} error={}", key, dir, System.currentTimeMillis() - time,
					e.getMessage());
			// ignore failed deletion
		}
	}

	@Override
	public Iterable<RevCommit> logRead(String group, String path, Integer skip, Integer max) throws GitAPIException {
		return log(group, path, gitRead(group), skip, max);
	}

	@Override
	public Iterable<RevCommit> logWrite(String group, String path, Integer skip, Integer max) throws GitAPIException {
		return log(group, path, gitWrite(group), skip, max);
	}

	private Iterable<RevCommit> log(String group, String path, Git git, Integer skip, Integer max)
			throws GitAPIException {
		long time = System.currentTimeMillis();
		LogCommand command = git.log();
		String normalizedPath = normalizeRead(group, path);
		if (normalizedPath != null) {
			command = command.addPath(normalizedPath);
		}
		if (skip != null) {
			command = command.setSkip(skip);
		}
		if (max != null) {
			command = command.setMaxCount(max);
		}
		Iterable<RevCommit> call = command.call();
		log.info("log({}):{}, time={}", group, normalizedPath, System.currentTimeMillis() - time);
		return call;
	}
}