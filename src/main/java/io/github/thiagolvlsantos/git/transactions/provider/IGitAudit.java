package io.github.thiagolvlsantos.git.transactions.provider;

public interface IGitAudit {

	String username();

	String email();

	IGitAudit INSTANCE = new IGitAudit() {

		@Override
		public String username() {
			return "";
		}

		@Override
		public String email() {
			return "";
		}
	};
}
