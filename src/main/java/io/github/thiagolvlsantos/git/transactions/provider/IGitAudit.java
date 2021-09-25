package io.github.thiagolvlsantos.git.transactions.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public interface IGitAudit {

	UserInfo author();

	UserInfo committer();

	IGitAudit INSTANCE = new IGitAudit() {

		private UserInfo empty = new UserInfo("", "");

		@Override
		public UserInfo author() {
			return empty;
		}

		@Override
		public UserInfo committer() {
			return empty;
		}
	};

	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public static class UserInfo {
		private String user;
		private String email;
	}
}
