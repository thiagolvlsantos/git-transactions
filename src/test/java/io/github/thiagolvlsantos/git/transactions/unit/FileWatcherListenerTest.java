package io.github.thiagolvlsantos.git.transactions.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import io.github.thiagolvlsantos.git.transactions.watcher.FileWatcherListener;
import io.github.thiagolvlsantos.git.transactions.watcher.Watcher;

@ContextConfiguration(classes = FileWatcherListenerTest.Config.class)
@SpringBootTest
class FileWatcherListenerTest {

	public static class Config {
		@Bean
		public FileWatcherListener listener() {
			return new FileWatcherListener();
		}
	}

	@Test
	void defined(@Autowired ApplicationContext ctx) throws Exception {
		FileWatcherListener s = ctx.getBean(FileWatcherListener.class);
		Path dir = Path.of("tmp");
		s.start("project", dir);
		Watcher w = s.ignore("project", dir);
		assertThat(w.getItems()).isEmpty();
	}
}
