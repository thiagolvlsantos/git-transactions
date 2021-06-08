package io.github.thiagolvlsantos.git.transactions.file;

import java.io.File;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@SuppressWarnings("serial")
public class FileItem implements Serializable {

	private File file;
	private EFileStatus status;
}