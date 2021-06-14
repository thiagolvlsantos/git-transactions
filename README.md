# git-transactions

[![CI with Maven](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/maven.yml/badge.svg)](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/maven.yml)
[![CI with CodeQL](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/codeql.yml/badge.svg)](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/codeql.yml)
[![CI with Sonar](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/sonar.yml/badge.svg)](https://github.com/thiagolvlsantos/git-transactions/actions/workflows/sonar.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_git-transactions&metric=alert_status)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_git-transactions)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=thiagolvlsantos_git-transactions&metric=coverage)](https://sonarcloud.io/dashboard?id=thiagolvlsantos_git-transactions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/git-transactions/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/git-transactions/)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)


## Transactions using Git repositories. 

Imagine you reading/writting locally to your file system attached to your Git repository as a transaction in your service class. An abstraction, using Aspects, to read and write files in your file system and automatically have it pulled/commited/pushed to your Git repository.

Welcome to ``git-transactions``, see bellow how simple it is.

## Usage

Include latest version [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.thiagolvlsantos/git-transactions/badge.svg)](https://repo1.maven.org/maven2/io/github/thiagolvlsantos/git-transactions/) to your project.

```xml
		<dependency>
			<groupId>io.github.thiagolvlsantos</groupId>
			<artifactId>git-transactions</artifactId>
			<version>${latestVersion}</version>
		</dependency>
```

## Add repositories configurations

To your ``application.properties`` add information about the repositories of interest.  The example bellow we have ``projects`` and ``deployments`` repositories.

```properties
# Shared configuration, if nothing more specific to a repo is used.
gitt.repository.user=thiagolvlsantos
gitt.repository.password=thiagospassword

# this repo bellongs to thiagolvlsantos, so the user and password above are reused
gitt.repository.projects.read=data/read/projects
gitt.repository.projects.write=data/write/projects
gitt.repository.projects.remote=https://github.com/thiagolvlsantos/gitt-example-projects.git

# any repo can have its own user and password, multiple repos from different users can compose a file
gitt.repository.deployments.user=anotheruser
gitt.repository.deployments.password=anotherpassword
gitt.repository.deployments.read=data/read/deployments
gitt.repository.deployments.write=data/write/deployments
gitt.repository.deployments.remote=https://github.com/thiagolvlsantos/gitt-example-deployments.git
```

## Add annotation ``@EnableGit``

```java
...
@EnableGit
public class Application {
	...main(String[] args) {...}
}
```

If it somehow does not work, for while add ``@ComponentScan`` to your Spring Application class.

```java
...
@ComponentScan("io.github.thiagolvlsantos.git")
public class Application {
	...main(String[] args) {...}
}
```


## Add a reference to ``GitServices`` and annotate your methods with ``@GitRead`` or ``@GitWrite``.

This following code shows how to read a file from Git which was automatically download by ``@GitRead(<repo_name>)`` annotation. Once the Git was downloaded the navigation through its structure is straightforward.

```java
...
private @Autowired GitServices gitServices;

...

@GitRead("projects")
public String readProjectFile(String projectName) {
	File dir = gitServices.readDirectory("projects");
	return Files.readString(new File(dir,projectName+".json").toPath());
}

...
```

If the user wants to send or update files or directories into a Git repository use ``@GitWrite(<repo_name>)`` and after method finalization the changes are automatically commited/pushed to the Git repository.

```java
...
private @Autowired GitServices gitServices;

...

@GitWrite("projects")
public void writeProjectFile(String projectName) {
	File dir = gitServices.writeDirectory("projects");
	File newFile = new File(dir,projectName+".json");
	String newContent = "{\"name\":\""+projectName+"\"}"}
	Files.write(newFile.toPath(), newContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
}

...
```

Multiple combinations of read/write are allowed for different repositories. When mixing read and write, read repository downloads are performed first.

A larger set of examples can be found in [test directory](https://github.com/thiagolvlsantos/git-transactions/tree/master/src/test/java/io/github/thiagolvlsantos/git/transactions/integration)

## Build

Localy, from this root directory call Maven commands or `bin/<script name>` at our will.
