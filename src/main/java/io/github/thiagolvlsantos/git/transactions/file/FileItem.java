package io.github.thiagolvlsantos.git.transactions.file;

import java.io.File;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@SuppressWarnings("serial")
public class FileItem implements Serializable {

	private File file;
	private EFileStatus status;
}